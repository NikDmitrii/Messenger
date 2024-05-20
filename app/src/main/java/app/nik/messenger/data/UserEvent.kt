package app.nik.messenger.data

sealed class UserEvent {
    data class UserAdded(val user: User) : UserEvent()
    data class UserRemoved(val user: User, val position: Int) : UserEvent()
    data class UserUpdated(val user: User, val position: Int) : UserEvent()
}