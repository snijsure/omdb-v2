package com.snijsure.omdbsearch.ui.view

import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.snijsure.omdbsearch.R
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerFragment

class AboutAppFragment : DaggerFragment() {

    @BindView(R.id.aboutText)
    lateinit var aboutText: TextView

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.about_dialog, container, false)
        ButterKnife.bind(this, rootView)
        aboutText.text = Html.fromHtml(resources.getString(R.string.about_text))
        return rootView
    }
}