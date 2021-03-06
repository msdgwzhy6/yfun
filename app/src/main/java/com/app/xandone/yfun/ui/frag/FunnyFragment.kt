package com.app.xandone.yfun.ui.frag

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.app.xandone.yfun.App
import com.app.xandone.yfun.R
import com.app.xandone.yfun.api.ApiConstants
import com.app.xandone.yfun.bean.FunBean
import com.app.xandone.yfun.ui.adapter.FunAdapter
import com.app.xandone.yfun.ui.base.BaseFragment
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.wilddog.client.*
import com.wilddog.wilddogcore.WilddogApp
import com.wilddog.wilddogcore.WilddogOptions

/**
 * author: xandone
 * created on: 2017/8/23 16:24
 */
class FunnyFragment : BaseFragment() {
    lateinit var mRecycle: RecyclerView
    lateinit var mRefresh: SmartRefreshLayout

    lateinit var funAdapter: FunAdapter
    var dataList = ArrayList<FunBean>()


    companion object {
        var mRef: SyncReference

        init {
            val mWilddogOptions = WilddogOptions.Builder().setSyncUrl(ApiConstants.FUN_URL).build()
            WilddogApp.initializeApp(App.sContext, mWilddogOptions)
            mRef = WilddogSync.getInstance().reference
        }
    }

    override fun getLayout(layoutId: Int): Int {
        return R.layout.frag_funny_layout
    }

    override fun initView(view: View?) {
        mRecycle = view?.findViewById(R.id.frag_fun_recycle) as RecyclerView
        mRefresh = view.findViewById(R.id.frag_fun_refresh) as SmartRefreshLayout
        funAdapter = FunAdapter(dataList, activity)
        mRecycle.adapter = funAdapter
        mRecycle.layoutManager = LinearLayoutManager(this.activity)

    }

    override fun initData() {
        getData(mRef)
        mRefresh.setOnRefreshListener {
            getData(mRef)
        }
    }


    fun getData(ref: SyncReference) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.value as Map<*, *>
                val list = value["results"] as List<*>
                dataList.clear()
                for (obj in list) {
                    val data = obj as Map<String, String>
                    if (!TextUtils.isEmpty(data["title"])) {
                        dataList.add(FunBean(data["title"], data["content"],
                                data["url"], data["mydate"]))
                    }
                }
                mRefresh.finishRefresh()
                funAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(syncError: SyncError) {
                mRefresh.finishRefresh()
            }
        })
    }

}