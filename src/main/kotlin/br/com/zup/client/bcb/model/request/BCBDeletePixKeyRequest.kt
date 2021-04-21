package br.com.zup.client.bcb.model.request

data class BCBDeletePixKeyRequest(
    val key: String,
    val participant: String
){
    companion object{
        fun of(key: String, participant: String): BCBDeletePixKeyRequest{
            return BCBDeletePixKeyRequest(
                key = key,
                participant = participant
            )
        }
    }
}
