package com.lykj.aextreme.afinal.adapter;

/**
 * Adapter的Item点击事件监听
 */
public class ViewOnClick implements android.view.View.OnClickListener {
    Object obj;
    int position;
    IAdapterListener listener;

    public ViewOnClick(IAdapterListener listener, Object obj, int position) {
        this.obj = obj;
        this.position = position;
        this.listener = listener;
    }

    @Override
    public void onClick(android.view.View v) {
        if (listener != null) {
            listener.onViewClick(v.getId(), obj, position);
        }
    }

}