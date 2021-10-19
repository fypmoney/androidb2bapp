package com.fyp.trackr.base

import android.net.Uri

fun getPath(uri: Uri?): String? {
    if (uri == null)
        return null
    val params = uri.pathSegments
    return if (params.size > 0)
        params[0]
    else null
}

fun getAction(uri: Uri?): String? {
    if (uri == null)
        return null
    val params = uri.pathSegments
    return if (params.size > 1)
        params[1]
    else null
}

fun getParam(uri: Uri?): String? {
    if (uri == null)
        return null
    return uri.getQueryParameter("id")
}