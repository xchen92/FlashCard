package com.xchen92.flashcard.ui.main

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.xchen92.flashcard.MainActivity.Companion.DELETE_ALL
import com.xchen92.flashcard.MainActivity.Companion.HIDE_COMPLETED
import com.xchen92.flashcard.MainActivity.Companion.HIDE_IMAGE
import com.xchen92.flashcard.MainActivity.Companion.HIDE_LABEL
import com.xchen92.flashcard.R
import com.xchen92.flashcard.database.Item
import com.xchen92.flashcard.databinding.CardItemBinding
import kotlinx.android.synthetic.main.card_item.view.*
import kotlinx.android.synthetic.main.main_fragment.*
import java.io.File
import java.util.*


class MainFragment : Fragment(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val picasso = Picasso.get()
    private lateinit var cardRecycler: RecyclerView
    private lateinit var adapter: CardRecyclerAdapter
    private val prefs: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(activity)
    }

    private val vm: MainViewModel  by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

   override fun onResume() {
        super.onResume()
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            HIDE_COMPLETED -> { updateList() }
            DELETE_ALL->{allItemDeletedAlert(adapter.getAllItems())
                with(prefs.edit()) {
                    putBoolean(DELETE_ALL, false)
                    apply()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateList()
        adapter = CardRecyclerAdapter()
        cardRecycler = view.findViewById(R.id.card_recycler)
        cardRecycler.layoutManager = LinearLayoutManager(context)
        cardRecycler.adapter = adapter
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val item = adapter.getItemAtPosition(viewHolder.adapterPosition)
                    vm.deleteItem(item)
                    itemDeletedAlert(item)
                }
            }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(cardRecycler)


        add_fab.setOnClickListener {
            AddItemFragment.showDialog(requireActivity().supportFragmentManager)
        }
    }
    private fun updateList(){
        if(prefs.getBoolean(HIDE_COMPLETED,false)){
            vm.partialCardLiveData.observe(viewLifecycleOwner, Observer { items ->
                adapter.updateItems(items)
            })
            }else{
            vm.cardLiveData.observe(viewLifecycleOwner, Observer { items ->
                adapter.updateItems(items)
            })

        }
    }

    private fun allItemDeletedAlert(items: List<Item>) {
        val msg = resources.getString(R.string.deleting_all)
        val builder = AlertDialog.Builder(requireContext())
        with(builder) {
            setTitle(R.string.alert)
            setMessage(msg)
            setIcon(R.drawable.alert_icon)
            val dialogClickListener =DialogInterface.OnClickListener { _: DialogInterface, i: Int ->
                    when(i){
                        DialogInterface.BUTTON_NEUTRAL->{
                            Toast.makeText(context,"Canceled.",
                                Toast.LENGTH_SHORT).show()
                        }
                        DialogInterface.BUTTON_POSITIVE->{
                            vm.deleteAll(items)
                            Toast.makeText(context,"Items Deleted.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            setPositiveButton(R.string.yes, dialogClickListener)
            setNeutralButton(R.string.cancel,dialogClickListener)
        }
        builder.show()
    }
    fun itemDeletedAlert(item: Item) {
        val msg = resources.getString(R.string.deleting, item.title)
        val builder = AlertDialog.Builder(requireContext())
        with(builder) {
            setTitle(R.string.alert)
            setMessage(msg)
            setIcon(R.drawable.alert_icon)
            setPositiveButton(R.string.ok, null)
            show()
        }

    }

    inner class CardRecyclerHolder(private val binding: CardItemBinding) :
        RecyclerView.ViewHolder(binding.root){

        private val photoView: ImageView = itemView.photo_imageView
        private val labelView: TextView = itemView.category_textView
        private val labelCheck: CheckBox = itemView.recycler_completed_checkBox
        private val recycleBG: ConstraintLayout =itemView.recycler_item_container
        private var cnt:Int=0
        init{
            itemView.setOnClickListener {
                val title = itemView.title_textView.text.toString()
                vm.getItemWithTitle(title)
               view?.findNavController()?.navigate(R.id.action_mainFragment_to_detailFragment)
            }
            //update status:complete?
            labelCheck.setOnCheckedChangeListener { buttonView, _ ->
                val id:UUID =adapter.getItemUUIDAtPosition(adapterPosition)
                  vm.updateItem(id,buttonView.isChecked)
               // Log.d("STATUS", id.toString());//debug
            }
        }

        fun bind(item: Item) {
            binding.item = item
            binding.apply {
                picasso.load(File(context?.filesDir, item.photoFileName))
                    .placeholder(item.typeImage)
                    .fit()
                    .centerCrop()
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(photoView)

                //follow the image view setting
                if(prefs.getBoolean(HIDE_IMAGE,false)){
                photoView.visibility = INVISIBLE
                }else{photoView.visibility = VISIBLE}

                //follow the label view setting
                if(prefs.getBoolean(HIDE_LABEL,false)){
                    labelView.visibility = INVISIBLE
                }else{labelView.visibility = VISIBLE}
                executePendingBindings()
                cnt = item.id.toInt()
                val s =(cnt%7).toString()
                Log.d("debug",s)
                val colorId = resources.getIdentifier(
                    "bg$s",
                    "color",
                    "com.xchen92.flashcard"
                )
                recycleBG.setBackgroundResource(colorId)

            }
        }

    }

    inner class CardRecyclerAdapter : RecyclerView.Adapter<CardRecyclerHolder>() {
        private var items: List<Item> = emptyList()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardRecyclerHolder =
            CardRecyclerHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.card_item, parent, false
                )
            )

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holderCard: CardRecyclerHolder, position: Int) {
            val item = items[position]
            holderCard.bind(item)
        }

        fun updateItems(newItems: List<Item>) {
            this.items = newItems
            notifyDataSetChanged()
        }

        fun getItemAtPosition(position: Int): Item {
            return items[position]
        }
        fun getItemUUIDAtPosition(position: Int):UUID{
            return items[position].uuid
        }

        fun getAllItems():List<Item>{
            return items
        }
    }
}
