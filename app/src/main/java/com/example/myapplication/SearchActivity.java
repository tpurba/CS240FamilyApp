package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

import Model.Event;
import Model.Person;

public class SearchActivity extends AppCompatActivity {

    private static final int EVENTS_ITEM_VIEW_TYPE = 0;
    private static final int PEOPLE_ITEM_VIEW_TYPE = 1;
    Datacache instance = Datacache.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //set the views
        EditText search = findViewById(R.id.searchText);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        //build the layout
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        TextWatcher textWatcher = new TextWatcher()
        {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchText = search.getText().toString();
                //get the lists
                List<Event> events = instance.searchEvents(searchText);
                List<Person> people = instance.searchPeople(searchText);
                SearchAdapter adapter = new SearchAdapter(events, people);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        search.addTextChangedListener(textWatcher);
    }
    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final List<Event> events;
        private final List<Person> people;

        SearchAdapter(List<Event> events, List<Person> people) {
            this.events = events;
            this.people = people;
        }

        @Override
        public int getItemViewType(int position) {
            return position < events.size() ? EVENTS_ITEM_VIEW_TYPE : PEOPLE_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if(viewType == EVENTS_ITEM_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.person_events, parent, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.family_persons, parent, false);
            }

            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if(position < events.size()) {
                holder.bind(events.get(position));
            } else {
                holder.bind(people.get(position - events.size()));
            }
        }

        @Override
        public int getItemCount() {
            return events.size() + people.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView fullEvent;
        private TextView fullName;
        private TextView personFullName;
        private ImageView genderView;

        private final int viewType;
        private Event event;
        private Person person;

        SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if(viewType == EVENTS_ITEM_VIEW_TYPE) {
                fullEvent = itemView.findViewById(R.id.fullEventInfo);
                fullName = itemView.findViewById(R.id.userFullName);
            } else {
                personFullName = itemView.findViewById(R.id.fullName);
                genderView = itemView.findViewById(R.id.genderPicture);
            }
        }

        private void bind(Event event) {
            this.event = event;
            String eventInfo = event.getEventType() + ": " + event.getCity() + " " +
                    event.getCountry() + " (" + event.getYear() + ")";
            Person temp_p = instance.findPerson(event.getPersonID());
            fullEvent.setText(eventInfo);
            String usersFullName = temp_p.getFirstName() + " " + temp_p.getLastName();
            fullName.setText(usersFullName);
        }

        private void bind(Person person) {
            this.person = person;
            String fullName = person.getFirstName() + " " + person.getLastName();
            personFullName.setText(fullName);
            if(person.getGender().equals("m"))
            {
                genderView.setImageResource(R.drawable.male);
            }
            else
            {
                genderView.setImageResource(R.drawable.female);
            }
        }

        @Override
        public void onClick(View view) {
            if(viewType == EVENTS_ITEM_VIEW_TYPE) {
                // This is were we could pass the skiResort to a ski resort detail activity

                Toast.makeText(SearchActivity.this, "TO the Event Activity!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), EventActivity.class).putExtra("EVENTID", event.getEventID()));
            } else {
                // This is were we could pass the hikingTrail to a hiking trail detail activity

                Toast.makeText(SearchActivity.this, "TO the Person Activity!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), PersonActivity.class).putExtra("PERSONID", person.getPersonID()));
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
}