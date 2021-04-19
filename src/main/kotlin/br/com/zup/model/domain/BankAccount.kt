package br.com.zup.model.domain

import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Embeddable
class BankAccount(

    @field:NotBlank
    @Column(nullable = false)
    val institute: String,

    @field:NotBlank
    @Column(nullable = false)
    val ispb: String,

    @field:NotBlank
    @field:Size(max = 4)
    @Column(nullable = false, length = 4)
    val agency: String,

    @field:NotBlank
    @field:Size(max = 6)
    @Column(nullable = false, length = 6)
    val number: String,

    @field:NotBlank
    @Column(nullable = false)
    val ownerName: String,

    @field:NotBlank
    @field:Size(max = 11)
    @Column(nullable = false, length = 11)
    val ownerCpf: String
)
