package app.nik.messenger.domain

import android.content.Context
import app.nik.messenger.data.User
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter


class UsersParser(private val fileName : String, private val context : Context)
{
    fun writeAllUsers(users : MutableList<User>) : Boolean
    {
        try
        {
            val gson = GsonBuilder().create()
            val jsonString = gson.toJson(users)

            val file = File(context.filesDir, fileName)

            FileWriter(file).use { writer ->
                writer.write(jsonString)
            }

            return true
        } catch (e: Exception)
        {
            e.printStackTrace()
            return false
        }
    }

    fun readUsers(): MutableList<User>
    {
        val users: MutableList<User> = mutableListOf()

        try
        {
            val file = File(context.filesDir, fileName)

            BufferedReader(FileReader(file)).use { reader ->
                val jsonString = reader.readText()

                // Преобразуем JSON строку в список объектов User
                val gson = GsonBuilder().create()
                val items: List<User> = gson.fromJson(jsonString, object : TypeToken<List<User>>() {}.type)

                // Добавляем все объекты из списка в результирующий список
                users.addAll(items)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return users
    }


    fun writeUser(student : User) : Boolean
    {
        try
        {
            val gson = GsonBuilder().create()
            val jsonString = gson.toJson(student)

            val file = File(context.filesDir, fileName)

            FileWriter(file).use { writer ->
                writer.write(jsonString)
            }

            return true
        } catch (e: Exception)
        {
            e.printStackTrace()
            return false
        }
    }
}