package com.cf.takephotolibrary;

/**
 * 描    述：
 * 创建日期：2017/7/6 14:28
 * 作    者：Chengfu
 * 邮    箱：
 * 备    注：
 */
public interface ResultListener {
    /**成功回调*/
    void onSuccess(String imagPath);
    /**取消回调*/
    void onCancel();
    /**失败回调*/
    void onFailure();
}
