package com.example.supertaller3

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.cardview.widget.CardView


class Useradapter(private val context: Context, private val users: List<User>) : BaseAdapter() {

    override fun getCount(): Int {
        return users.size
    }

    override fun getItem(position: Int): Any {
        return users[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val user = users[position]

        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.contactos, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        // Aquí estableces el nombre del usuario en el TextView del CardView
        viewHolder.userNameTextView.text = user.nombre+" "+user.apellido

        view


        viewHolder.cardpaises.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, MapsActivity::class.java)
            intent.putExtra("USER_EMAIL", user.email)
            context.startActivity(intent)
        })
        return view
    }

    private class ViewHolder(view: View) {
        val userNameTextView: TextView = view.findViewById(R.id.nombrepais)
        val cardpaises: CardView = view.findViewById(R.id.cardpaises)
    }
}
