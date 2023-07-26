package clarity.backend.util

import clarity.backend.entity.*

class RecommendedSets {
    companion object {
        fun buildRecommendedSets() {
            try {
                val user = UserEntity().getUser("Clarity")

                if(user.user != null) {
                    return;
                }

                val createClarityUserResp = UserEntity().createUser(
                    CreateUserEntity(
                        username = "Clarity",
                        email = "clarity@gmail.com",
                        password = "clarity",
                        firstname = "Clarity",
                        lastname = "Org",
                        phone_number = "000000000",
                        difficulty = Difficulty.Easy
                    )
                )

                val userId = createClarityUserResp.userId!!

                val phrasesSetOneTitle = "/th Sentences"
                val cardPhrasesOne = mutableListOf<String>(
                    "Weather",
                    "Zenith",
                    "The thief thwarted the sleuth.",
                    "The smoothie in the thermos was thirst-quenching",
                    "They gathered thirty threadbare thespians in the theatre",
                    "Thanksgiving is now the third Thursday of the month",
                )

                val phrasesSetTwoTitle = "4 Syllable"
                val cardPhrasesTwo = mutableListOf<String>("discovery", "appreciate", "questionable", "librarian", "apologize")

                val createSetOneResp = CardSetEntity().createCardSet(
                    CreateCardSetEntity(creator_id = userId, phrasesSetOneTitle, type = "practise", 0)
                )
                val setOneMetadata = createSetOneResp.set!!

                for (phrase in cardPhrasesOne) {
                    val createCardResp = CardEntity().createCard(
                        CreateCardEntity(phrase = phrase, title = phrase, setId = setOneMetadata.set_id)
                    )
                }

                val createSetTwoResp = CardSetEntity().createCardSet(
                    CreateCardSetEntity(creator_id = userId, phrasesSetTwoTitle, type = "practise", 0)
                )
                val setTwoMetatdata = createSetTwoResp.set!!
                for (phrase in cardPhrasesTwo) {
                    val createCardResp = CardEntity().createCard(
                        CreateCardEntity(phrase = phrase, title = phrase, setId = setTwoMetatdata.set_id)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}