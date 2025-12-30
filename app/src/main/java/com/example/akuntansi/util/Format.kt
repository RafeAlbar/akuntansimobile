package com.example.akuntansi.util

// [CHANGES] ganti Int -> Long supaya aman untuk nominal besar
fun formatRupiah(value: Long): String {
    val s = value.toString()
    val sb = StringBuilder()
    var count = 0
    for (i in s.length - 1 downTo 0) {
        sb.append(s[i])
        count++
        if (count % 3 == 0 && i != 0) sb.append('.')
    }
    return sb.reverse().toString()
}

// [CHANGES] overload biar kode lama yang kirim Int tetap jalan
fun formatRupiah(value: Int): String = formatRupiah(value.toLong())
