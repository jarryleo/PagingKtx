package cn.leo.retrofit_ktx.http

import kotlinx.coroutines.Job

/**
 * @author : ling luo
 * @date : 2019-11-18
 * Job委托类，去泛型
 */
class KJob<T>(val job: Job) : Job by job