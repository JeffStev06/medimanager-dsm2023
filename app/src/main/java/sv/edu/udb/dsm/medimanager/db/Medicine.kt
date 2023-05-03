package sv.edu.udb.dsm.medimanager.db

data class Medicine (
    val id: Int,
    val name: String,
    val dose: String,
    val doctor: String,
    val idInterval: Int,
    val interval: Int,
    val time: String,
    val dateStart: String,
    val dateEnd: String
) {
    // Implementar un get Date y un Time
}