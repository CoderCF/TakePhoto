package com.cf.takephotolibrary.listener;

public interface ResultListener {
    /**成功回调*/
    void onSuccess(String imagePath);
    /**取消回调*/
    void onCancel();
    /**失败回调*/
    void onFailure();
}
