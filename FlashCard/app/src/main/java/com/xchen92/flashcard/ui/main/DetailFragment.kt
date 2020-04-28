package com.xchen92.flashcard.ui.main
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.xchen92.flashcard.R
import kotlinx.android.synthetic.main.detail_fragment.*
import java.io.File


class DetailFragment : Fragment() {

    private val vm: MainViewModel by activityViewModels()
    private val picasso = Picasso.get()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.detail_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var cnt:Int =0
        var defaultImage= R.drawable.work
        super.onViewCreated(view, savedInstanceState)
        vm.currentProduct.observe(viewLifecycleOwner, Observer {item->
            title_textView.text=item?.title
            category_textView.text =item?.typeName
            defaultImage =item.typeImage
            when(item.completed){
                true->{
                    status_textView.text =getString(R.string.status_completed)
                    cheer_up_textView.text =getString(R.string.status_good_job)
                }
                false->{
                    status_textView.text =getString(R.string.status_uncompleted)
                    cheer_up_textView.text =getString(R.string.lets_do_this)}
            }
            val photoView = photo_imageView
            picasso.load(File(context?.filesDir, item.photoFileName))
                .placeholder(item.typeImage)
                .fit()
                .centerCrop()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(photoView)

        })

        //customize
        photo_imageView.setOnClickListener{
            cnt++
            if(cnt%2==0){
                photo_imageView.setImageResource(defaultImage)
            }else{
            val rnds = (1 until 7).random()
            val drawableId = resources.getIdentifier(
                "cheese$rnds",
                "drawable",
               "com.xchen92.flashcard"
            )
            photo_imageView.setImageResource(drawableId)}
        }
        closeDetailButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_detailFragment_to_mainFragment)
        }

    }

}
