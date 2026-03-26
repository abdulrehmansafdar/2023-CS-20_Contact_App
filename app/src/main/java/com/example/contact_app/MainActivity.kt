package com.example.contact_app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactAdapter
    private lateinit var searchView: SearchView
    private lateinit var spinnerFilter: Spinner
    private val contacts = mutableListOf<Contact>()
    private val filteredContacts = mutableListOf<Contact>()
    private val contactTimes = mutableListOf<String>() // Store time of saving

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewContacts)
        searchView = findViewById(R.id.searchView)
        spinnerFilter = findViewById(R.id.spinnerFilter)
        adapter = ContactAdapter(filteredContacts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val fabAddContact: FloatingActionButton = findViewById(R.id.fabAddContact)
        fabAddContact.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
            startActivityForResult(intent, 101)
        }

        // Setup filter spinner (A-Z and All)
        val filterOptions = mutableListOf("All")
        filterOptions.addAll(('A'..'Z').map { it.toString() })
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = spinnerAdapter

        spinnerFilter.setSelection(0)
        spinnerFilter.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                filterAndSearch()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterAndSearch()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterAndSearch()
                return true
            }
        })
    }

    private fun filterAndSearch() {
        val query = searchView.query?.toString()?.trim()?.lowercase() ?: ""
        val filter = spinnerFilter.selectedItem?.toString() ?: "All"
        filteredContacts.clear()
        filteredContacts.addAll(contacts.filter { contact ->
            val matchesSearch = query.isEmpty() ||
                contact.name.lowercase().contains(query) ||
                contact.number.contains(query)
            val matchesFilter = filter == "All" || contact.name.startsWith(filter, ignoreCase = true)
            matchesSearch && matchesFilter
        })
        adapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null) {
            val name = data.getStringExtra("name") ?: return
            val number = data.getStringExtra("number") ?: return
            val imageUriString = data.getStringExtra("imageUri") ?: return
            val imageUri = Uri.parse(imageUriString)
            val timeSaved = data.getStringExtra("timeSaved") ?: ""
            val contact = Contact(name, number, imageUri)
            contacts.add(contact)
            contactTimes.add(timeSaved)
            filterAndSearch()
        }
    }
}