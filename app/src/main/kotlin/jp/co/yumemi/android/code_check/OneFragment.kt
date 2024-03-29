/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.databinding.FragmentOneBinding
import jp.co.yumemi.android.code_check.function.launchRepeatOnStarted
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class OneFragment: Fragment(R.layout.fragment_one){
    private val vm by viewModels<OneViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val _binding= FragmentOneBinding.bind(view)

        val context = requireContext()

        val _layoutManager= LinearLayoutManager(context)
        val _dividerItemDecoration=
            DividerItemDecoration(context, _layoutManager.orientation)
        val _adapter= CustomAdapter(object : CustomAdapter.OnItemClickListener{
            override fun itemClick(item: Item){
                gotoRepositoryFragment(item)
            }
        })

        _binding.searchInputText
            .setOnEditorActionListener{ editText, action, _ ->
                if (action== EditorInfo.IME_ACTION_SEARCH){
                    editText.text.toString().let {
                        vm.search(it)
                    }
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

        _binding.recyclerView.also{
            it.layoutManager= _layoutManager
            it.addItemDecoration(_dividerItemDecoration)
            it.adapter= _adapter
        }

        launchRepeatOnStarted {
            vm.searchResultFlow.collect {
                _adapter.submitList(it)
            }
        }
    }

    fun gotoRepositoryFragment(item: Item)
    {
        val _action= OneFragmentDirections
            .actionRepositoriesFragmentToRepositoryFragment(item)
        findNavController().navigate(_action)
    }
}

val diff_util= object: DiffUtil.ItemCallback<Item>(){
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean
    {
        return oldItem.name== newItem.name
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean
    {
        return oldItem== newItem
    }

}

class CustomAdapter(
    private val itemClickListener: OnItemClickListener,
) : ListAdapter<Item, CustomAdapter.ViewHolder>(diff_util){

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)

    interface OnItemClickListener{
    	fun itemClick(item: Item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
    	val _view= LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item, parent, false)
    	return ViewHolder(_view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
    	val _item= getItem(position)
        (holder.itemView.findViewById<View>(R.id.repositoryNameView) as TextView).text=
            _item.name

    	holder.itemView.setOnClickListener{
     		itemClickListener.itemClick(_item)
    	}
    }
}
