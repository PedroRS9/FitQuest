package es.ulpgc.pigs.fitquest.screens.chatscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.ulpgc.pigs.fitquest.data.ChatRepository
import es.ulpgc.pigs.fitquest.data.FirebaseChatRepository
import es.ulpgc.pigs.fitquest.data.Message
import es.ulpgc.pigs.fitquest.data.Result
import java.util.Date

class ChatViewModel : ViewModel() {
    private val ChatRepository: ChatRepository = FirebaseChatRepository()
    private val _chatState = MutableLiveData<Result>()
    val chatState: LiveData<Result> = _chatState

    private var ChatId: String = ""

    fun getChatId(doctorId: String, userId: String){
        _chatState.value = Result.Loading
        ChatRepository.getChatId(doctorId, userId){ result ->
            if(result != null){
                ChatId = result
                getMessages()
            } else {
                _chatState.value = Result.Error(Exception("Chat not found."))
            }
        }
    }

    private fun getMessages(){
        ChatRepository.getMessages(ChatId){ result ->
            _chatState.value = result
        }
    }

    fun sendMessage(username: String, content: String){
        _chatState.value = Result.Loading
        val message = Message(content = content, senderUsername = username, timestamp = Date())
        ChatRepository.sendMessage(ChatId, username, message){ result ->
            _chatState.value = result
        }
    }

}
