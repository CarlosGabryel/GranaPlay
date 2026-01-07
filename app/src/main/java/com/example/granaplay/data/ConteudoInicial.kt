package com.example.granaplay.data

// Estruturas auxiliares apenas para facilitar a criação dos dados
data class SeedModulo(val id: Int, val titulo: String, val licoes: List<SeedLicao>)
data class SeedLicao(val id: Int, val titulo: String, val questoes: List<SeedQuestao>)
data class SeedQuestao(
    val tipo: String,
    val enunciado: String,
    val options: List<String>,
    val correctIndex: Int,
    val images: List<String>? = null
)

object ConteudoInicial {
    val dados = listOf(
        SeedModulo(
            1, "Conhecendo o Dinheiro", listOf(
                SeedLicao(101, "O que são moedas?", listOf(
                    SeedQuestao("TEXT_2", "As MOEDAS são feitas de metal. Elas são duras e fazem 'plim' quando caem no chão! Você entendeu?", listOf("Entendi, são de metal!", "Não, são de algodão."), 0),
                    SeedQuestao("IMAGE_4", "Toque na imagem que mostra uma MOEDA de verdade:", listOf("Moeda de 1 Real", "Botão de camisa", "Tampinha de garrafa", "Pedra redonda"), 0, listOf("img_moeda_real", "img_botao", "img_tampinha", "img_pedra")),
                    SeedQuestao("TEXT_2", "Uma moeda grande sempre vale mais que uma moeda pequena?", listOf("Nem sempre! Tamanho não é valor.", "Sim, a maior sempre vale mais."), 0),
                    SeedQuestao("TEXT_2", "Se você balançar o cofrinho e ouvir 'tlim tlim', o que tem dentro?", listOf("Muitas moedas", "Notas de papel"), 0)
                )),
                SeedLicao(102, "O que são notas?", listOf(
                    SeedQuestao("TEXT_2", "As NOTAS valem mais que moedas. Do que elas são feitas?", listOf("De papel especial", "De plástico duro"), 0),
                    SeedQuestao("TEXT_2", "Como a nota é de papel, o que acontece se ela cair na água?", listOf("Ela estraga e rasga", "Ela vira duas notas"), 0),
                    SeedQuestao("IMAGE_4", "Geralmente, qual destes vale mais dinheiro para comprar coisas?", listOf("Nota de 10 Reais", "Moeda de 50 centavos", "Moeda de 10 centavos", "Moeda de 5 centavos"), 0, listOf("img_nota_10", "img_moeda_50", "img_moeda_10", "img_moeda_05")),
                    SeedQuestao("TEXT_2", "Como diferenciamos o valor das notas?", listOf("Pela cor e pelo animal desenhado", "Todas são iguais"), 0)
                )),
                SeedLicao(103, "Contando o troco", listOf(
                    SeedQuestao("TEXT_2", "O que é TROCO?", listOf("É o dinheiro que sobra e volta pra mim", "É o preço do produto"), 0),
                    SeedQuestao("TEXT_2", "O suco custa 5 reais. Você pagou com uma nota de 10. Você recebe troco?", listOf("Sim, sobra dinheiro", "Não, o vendedor fica com tudo"), 0),
                    SeedQuestao("TEXT_2", "Se você der o dinheiro certinho do preço (trocado), sobra alguma coisa?", listOf("Não, não tem troco", "Sim, sempre tem troco"), 0),
                    SeedQuestao("IMAGE_4", "O vendedor não tinha moedas. O que NÃO serve como troco?", listOf("Balas e chicletes", "Moeda de 1 real", "Nota de 2 reais", "Moeda de 50 centavos"), 0, listOf("img_balas", "img_moeda_1", "img_nota_2", "img_moeda_50"))
                )),
                SeedLicao(104, "Dinheiro Digital", listOf(
                    SeedQuestao("TEXT_2", "Hoje em dia, podemos pagar sem pegar nas notas de papel. Isso é verdade?", listOf("Sim, usando cartão ou celular", "Não, só existe dinheiro de papel"), 0),
                    SeedQuestao("IMAGE_4", "Onde passamos o cartão para pagar na loja?", listOf("Maquininha de cartão", "Torradeira", "Calculadora", "Controle remoto"), 0, listOf("img_maquininha", "img_torradeira", "img_calculadora", "img_controle")),
                    SeedQuestao("TEXT_2", "Quando passamos o cartão, de onde sai o dinheiro?", listOf("Do nosso dinheiro no banco", "O cartão paga de graça"), 0),
                    SeedQuestao("IMAGE_4", "Qual aparelho usamos para fazer um PIX?", listOf("Celular (Smartphone)", "Microondas", "Relógio de parede", "Livro"), 0, listOf("img_celular", "img_microondas", "img_relogio", "img_livro"))
                ))
            )
        ),
        SeedModulo(
            2, "A Origem da Grana", listOf(
                SeedLicao(201, "Ganhando a Mesada", listOf(
                    SeedQuestao("TEXT_2", "O que é MESADA?", listOf("Dinheiro que os pais dão para ensinar a usar", "Um presente de aniversário"), 0),
                    SeedQuestao("TEXT_2", "A mesada é infinita (nunca acaba)?", listOf("Não, ela acaba se gastar tudo", "Sim, posso comprar o mundo"), 0),
                    SeedQuestao("TEXT_2", "Se você gastar toda a mesada em doces hoje, o que acontece amanhã?", listOf("Fico sem dinheiro", "Ela aparece no bolso"), 0),
                    SeedQuestao("IMAGE_4", "Qual a melhor atitude para fazer com parte da mesada?", listOf("Guardar no cofrinho", "Rasgar", "Perder na rua", "Esconder no lixo"), 0, listOf("img_cofrinho", "img_papel_rasgado", "img_rua", "img_lixo"))
                )),
                SeedLicao(202, "Trabalho e Recompensa", listOf(
                    SeedQuestao("TEXT_2", "De onde vem o dinheiro?", listOf("Do trabalho e esforço", "Nasce em árvore"), 0),
                    SeedQuestao("IMAGE_4", "Qual destas ações é um 'trabalho' que ajuda em casa?", listOf("Arrumar a cama", "Dormir o dia todo", "Assistir TV", "Comer brigadeiro"), 0, listOf("img_cama_arrumada", "img_dormindo", "img_tv", "img_brigadeiro")),
                    SeedQuestao("TEXT_2", "Quando você ganha uma moeda por ajudar, isso significa que:", listOf("Seu esforço tem valor", "Foi sorte"), 0),
                    SeedQuestao("TEXT_2", "Para ganhar dinheiro honestamente, eu preciso...", listOf("Fazer algo útil", "Ficar parado"), 0)
                )),
                SeedLicao(203, "O que é Salário?", listOf(
                    SeedQuestao("TEXT_2", "Criança ganha mesada. E o adulto que trabalha, ganha o quê?", listOf("Salário", "Mesada também"), 0),
                    SeedQuestao("IMAGE_4", "Quem recebe salário no final do mês?", listOf("A médica que trabalhou", "O gato que dormiu", "O bebê", "O boneco"), 0, listOf("img_medica", "img_gato", "img_bebe", "img_boneco")),
                    SeedQuestao("TEXT_2", "Para que serve o salário dos pais?", listOf("Pagar as contas da casa e comida", "Jogar aviõezinhos de papel"), 0),
                    SeedQuestao("TEXT_2", "O salário cai do céu?", listOf("Não, precisa trabalhar para receber", "Sim, cai todo dia"), 0)
                ))
            )
        )
    )
}