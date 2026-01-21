package com.example.granaplay.data

/**
 * Enumeração para os tipos de layout de questão suportados.
 * Evita o uso de strings soltas (magic strings).
 */
enum class TipoQuestao {
    TEXT_2,  // Pergunta de texto com 2 alternativas
    IMAGE_4  // Pergunta com 4 alternativas visuais (imagens)
}

// --- Classes de Dados para Seeding (Povoamento inicial) ---

data class SeedModulo(
    val id: Int,
    val titulo: String,
    val descricao: String,
    val licoes: List<SeedLicao>
)

data class SeedLicao(
    val id: Int,
    val titulo: String,
    val questoes: List<SeedQuestao>
)

/**
 * Representação transitória de uma questão para carga inicial.
 *
 * @param correctIndex Índice da resposta correta na lista [options].
 * Importante: O app deve embaralhar as alternativas ao exibir para o usuário,
 * pois aqui a correta é sempre definida como 0 para facilitar o cadastro.
 */
data class SeedQuestao(
    val tipo: TipoQuestao,
    val enunciado: String,
    val options: List<String>, // Textos das alternativas
    val correctIndex: Int,
    val images: List<String>? = null // Nomes dos resources (drawables), se houver
)

/**
 * Objeto Singleton contendo os dados estáticos para a primeira execução do App.
 * Estrutura: Módulos -> Lições -> Questões.
 */
object ConteudoInicial {
    val dados = listOf(

        // --- MÓDULO 1: Introdução ---
        SeedModulo(
            id = 1,
            titulo = "Conhecendo o Dinheiro",
            descricao = "Primeiros passos com o dinheiro",
            licoes = listOf(
                SeedLicao(
                    id = 101,
                    titulo = "O que são moedas?",
                    questoes = listOf(
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "As MOEDAS são feitas de metal. Elas são duras e fazem 'plim' quando caem no chão! Você entendeu?",
                            listOf("Entendi, são de metal!", "Não, são de algodão."),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.IMAGE_4,
                            "Toque na imagem que mostra uma MOEDA de verdade:",
                            listOf("Moeda de 1 Real", "Botão de camisa", "Tampinha de garrafa", "Pedra redonda"),
                            0,
                            listOf("ic_one_coin", "ic_button", "ic_bottle_cap", "ic_rock")
                        ),
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "Uma moeda grande sempre vale mais que uma moeda pequena?",
                            listOf("Nem sempre! Tamanho não é valor.", "Sim, a maior sempre vale mais."),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "Se você balançar o cofrinho e ouvir 'tlim tlim', o que tem dentro?",
                            listOf("Muitas moedas", "Notas de papel"),
                            0
                        )
                    )
                ),
                SeedLicao(
                    id = 102,
                    titulo = "O que são notas?",
                    questoes = listOf(
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "As NOTAS valem mais que moedas. Do que elas são feitas?",
                            listOf("De papel especial", "De plástico duro"),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "Como a nota é de papel, o que acontece se ela cair na água?",
                            listOf("Ela estraga e rasga", "Ela vira duas notas"),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.IMAGE_4,
                            "Geralmente, qual destes vale mais dinheiro para comprar coisas?",
                            listOf("Nota de 10 Reais", "Moeda de 50 centavos", "Moeda de 10 centavos", "Moeda de 5 centavos"),
                            0,
                            listOf("ic_10_reais", "ic_50_centavos", "ic_10_centavos", "ic_5_centavos")
                        ),
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "Como diferenciamos o valor das notas?",
                            listOf("Pela cor e pelo animal desenhado", "Todas são iguais"),
                            0
                        )
                    )
                ),
                SeedLicao(
                    id = 103,
                    titulo = "Contando o troco",
                    questoes = listOf(
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "O que é TROCO?",
                            listOf("É o dinheiro que sobra e volta pra mim", "É o preço do produto"),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "O suco custa 5 reais. Você pagou com uma nota de 10. Você recebe troco?",
                            listOf("Sim, sobra dinheiro", "Não, o vendedor fica com tudo"),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "Se você der o dinheiro certinho do preço (trocado), sobra alguma coisa?",
                            listOf("Não, não tem troco", "Sim, sempre tem troco"),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.IMAGE_4,
                            "O vendedor não tinha moedas. O que NÃO serve como troco?",
                            listOf("Balas e chicletes", "Moeda de 1 real", "Nota de 2 reais", "Moeda de 50 centavos"),
                            0,
                            listOf("ic_doces", "ic_one_coin", "ic_2_reais", "ic_50_centavos")
                        )
                    )
                ),
                SeedLicao(
                    id = 104,
                    titulo = "Dinheiro Digital",
                    questoes = listOf(
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "Hoje em dia, podemos pagar sem pegar nas notas de papel. Isso é verdade?",
                            listOf("Sim, usando cartão ou celular", "Não, só existe dinheiro de papel"),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.IMAGE_4,
                            "Onde passamos o cartão para pagar na loja?",
                            listOf("Maquininha de cartão", "Torradeira", "Calculadora", "Controle remoto"),
                            0,
                            listOf("ic_maquineta", "ic_torradeira", "ic_calculadora", "ic_controle")
                        ),
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "Quando passamos o cartão, de onde sai o dinheiro?",
                            listOf("Do nosso dinheiro no banco", "O cartão paga de graça"),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.IMAGE_4,
                            "Qual aparelho usamos para fazer um PIX?",
                            listOf("Celular (Smartphone)", "Microondas", "Relógio de parede", "Livro"),
                            0,
                            listOf("ic_celular", "ic_microondas", "ic_relogio", "ic_livros")
                        )
                    )
                )
            )
        ),

        // --- MÓDULO 2: Origem do Dinheiro ---
        SeedModulo(
            id = 2,
            titulo = "A Origem da Grana",
            descricao = "Como o dinheiro chega até você",
            licoes = listOf(
                SeedLicao(
                    id = 201,
                    titulo = "Ganhando a Mesada",
                    questoes = listOf(
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "O que é MESADA?",
                            listOf("Dinheiro que os pais dão para ensinar a usar", "Um presente de aniversário"),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "A mesada é infinita (nunca acaba)?",
                            listOf("Não, ela acaba se gastar tudo", "Sim, posso comprar o mundo"),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "Se você gastar toda a mesada em doces hoje, o que acontece amanhã?",
                            listOf("Fico sem dinheiro", "Ela aparece no bolso"),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.IMAGE_4,
                            "Qual a melhor atitude para fazer com parte da mesada?",
                            listOf("Guardar no cofrinho", "Rasgar", "Perder na rua", "Esconder no lixo"),
                            0,
                            listOf("img_cofrinho", "img_papel_rasgado", "img_rua", "img_lixo")
                        )
                    )
                ),
                SeedLicao(
                    id = 202,
                    titulo = "Trabalho e Recompensa",
                    questoes = listOf(
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "De onde vem o dinheiro?",
                            listOf("Do trabalho e esforço", "Nasce em árvore"),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.IMAGE_4,
                            "Qual destas ações é um 'trabalho' que ajuda em casa?",
                            listOf("Arrumar a cama", "Dormir o dia todo", "Assistir TV", "Comer brigadeiro"),
                            0,
                            listOf("img_cama_arrumada", "img_dormindo", "img_tv", "img_brigadeiro")
                        ),
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "Quando você ganha uma moeda por ajudar, isso significa que:",
                            listOf("Seu esforço tem valor", "Foi sorte"),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "Para ganhar dinheiro honestamente, eu preciso...",
                            listOf("Fazer algo útil", "Ficar parado"),
                            0
                        )
                    )
                ),
                SeedLicao(
                    id = 203,
                    titulo = "O que é Salário?",
                    questoes = listOf(
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "Criança ganha mesada. E o adulto que trabalha, ganha o quê?",
                            listOf("Salário", "Mesada também"),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.IMAGE_4,
                            "Quem recebe salário no final do mês?",
                            listOf("A médica que trabalhou", "O gato que dormiu", "O bebê", "O boneco"),
                            0,
                            listOf("img_medica", "img_gato", "img_bebe", "img_boneco")
                        ),
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "Para que serve o salário dos pais?",
                            listOf("Pagar as contas da casa e comida", "Jogar aviõezinhos de papel"),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "O salário cai do céu?",
                            listOf("Não, precisa trabalhar para receber", "Sim, cai todo dia"),
                            0
                        )
                    )
                )
            )
        ),

        // --- MÓDULO 3: Poupando ---
        SeedModulo(
            id = 3,
            titulo = "Poupando pro Futuro",
            descricao = "Protegendo o que você conquistou",
            licoes = listOf(
                SeedLicao(
                    id = 301,
                    titulo = "O Poder do Cofrinho",
                    questoes = listOf(
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "O que significa 'poupar'?",
                            listOf("Guardar um pouco hoje para usar depois", "Gastar tudo agora"),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.IMAGE_4,
                            "Qual destes objetos serve para guardar dinheiro?",
                            listOf("Cofrinho", "Panela", "Sapato furado", "Balde de água"),
                            0,
                            listOf("img_cofrinho", "img_panela", "img_sapato", "img_balde")
                        ),
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "Se você guardar 1 moeda todo dia, o que acontece no final do mês?",
                            listOf("Terei muitas moedas!", "As moedas somem"),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "Poupar ajuda a gente a:",
                            listOf("Realizar sonhos maiores", "Ficar triste"),
                            0
                        )
                    )
                )
            )
        ),

        // --- MÓDULO 4: Consumo Consciente ---
        SeedModulo(
            id = 4,
            titulo = "Comprando Bem",
            descricao = "Aprenda a gastar com consciência",
            licoes = listOf(
                SeedLicao(
                    id = 401,
                    titulo = "Quero ou Preciso?",
                    questoes = listOf(
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "Você precisa de água para viver. Isso é uma:",
                            listOf("Necessidade", "Brincadeira"),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.IMAGE_4,
                            "O que é mais importante comprar primeiro?",
                            listOf("Comida saudável", "Muitos doces", "Brinquedo caro", "Videogame"),
                            0,
                            listOf("img_comida", "img_doces", "img_brinquedo", "img_videogame")
                        ),
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "Antes de comprar algo caro, o que devemos fazer?",
                            listOf("Pensar se realmente precisamos", "Chorar na loja"),
                            0
                        ),
                        SeedQuestao(
                            TipoQuestao.TEXT_2,
                            "Dinheiro nasce em árvore?",
                            listOf("Não, precisa de esforço para ganhar", "Sim, é só plantar"),
                            0
                        )
                    )
                )
            )
        )
    )
}