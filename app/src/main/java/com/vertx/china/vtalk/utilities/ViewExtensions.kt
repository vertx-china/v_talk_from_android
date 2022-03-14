package com.vertx.china.vtalk.utilities

import android.widget.EditText

fun EditText.isEmpty(): Boolean {
    return this.text.toString().trim() == ""
}