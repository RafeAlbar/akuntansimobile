package com.example.akuntansi.data.model.laporan

data class NeracaGroupDto(
    val aset: List<NeracaRowDto> = emptyList(),
    val liabilitas: List<NeracaRowDto> = emptyList(),
    val ekuitas: List<NeracaRowDto> = emptyList()
)
