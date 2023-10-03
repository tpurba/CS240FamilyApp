package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.Pair;
import java.util.List;
import Model.Event;
import Model.Person;

public class PersonActivity extends AppCompatActivity {
    final String PERSON_KEY = "PERSONID";
    String personId = null;
    //textview
    TextView firstNameView;
    TextView lastNameView;
    TextView genderView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        Intent intent = getIntent();
        personId = (String)intent.getExtras().get(PERSON_KEY);
        if(personId != null)
        {
            Datacache instance = Datacache.getInstance();
            Person person = instance.findPerson(personId);
            firstNameView = findViewById(R.id.firstName);
            lastNameView = findViewById(R.id.lastName);
            genderView = findViewById(R.id.Gender);
            firstNameView.setText(person.getFirstName());
            lastNameView.setText(person.getLastName());
            if(person.getGender().equals("m"))
            {
                genderView.setText("Male");
            }
            else
            {
                genderView.setText("Female");
            }
            //get the lists
            List<Event> personEvents = instance.getPersonEvents(personId);
            instance.setRelation(personId);
            List<Pair<String,String>> relations = instance.getRelation();
            //set the view for expandable list view
            ExpandableListView expandableListView = findViewById(R.id.expandableList);
            //get the list
            expandableListView.setAdapter(new ExpandableListAdapter(relations, personEvents, person));
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
    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int LIFE_EVENTS_GROUP_POSITION = 0;
        private static final int FAMILY_GROUP_POSITION = 1;

        private final List<Pair<String,String>> relations;
        private final List<Event> personEvents;
        private final Person person;
        private Datacache instance = Datacache.getInstance();

        ExpandableListAdapter(List<Pair<String,String>> relations, List<Event> personEvents, Person person) {
            this.relations = relations;
            this.personEvents = personEvents;
            this.person = person;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                //swap these two
                case LIFE_EVENTS_GROUP_POSITION:
                    if(personEvents == null)
                    {
                        return 0;
                    }
                    return personEvents.size();
                case FAMILY_GROUP_POSITION:
                    return relations.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            // Not used
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // Not used
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_titles, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    titleView.setText(R.string.lifeEventsTitle);
                    break;
                case FAMILY_GROUP_POSITION:
                    titleView.setText(R.string.familyTitle);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.person_events, parent, false);
                    initializeLifeEventsView(itemView, childPosition);
                    break;
                case FAMILY_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.family_persons, parent, false);
                    initializeFamilyView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeLifeEventsView(View lifeEventsView, final int childPosition) {
            TextView eventInfoView = lifeEventsView.findViewById(R.id.fullEventInfo);
            //set the event
            Event temp_e = personEvents.get(childPosition);
            //based on event make eventinfo
            String eventInfo = temp_e.getEventType() + ": " + temp_e.getCity() + " " +
                    temp_e.getCountry() + " (" + temp_e.getYear() +")";
            //make the useres full name
            String usersFullName = person.getFirstName() + " " + person.getLastName();

            eventInfoView.setText(eventInfo);

            TextView fullNameView = lifeEventsView.findViewById(R.id.userFullName);
            fullNameView.setText(usersFullName);

            lifeEventsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PersonActivity.this, "Going to Event activity!!!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), EventActivity.class).putExtra("EVENTID", personEvents.get(childPosition).getEventID()));
                }
            });
        }

        private void initializeFamilyView(View familyView, final int childPosition) {
            //define views
            ImageView genderView = familyView.findViewById(R.id.genderPicture);
            TextView fullNameView = familyView.findViewById(R.id.fullName);
            TextView relationView = familyView.findViewById(R.id.relation);
            //set strings
            Person temp_p = instance.findPerson(relations.get(childPosition).getValue());
            String relationship = relations.get(childPosition).getKey();
            String fullName = temp_p.getFirstName() + " " + temp_p.getLastName();
            //update the views
            if(temp_p.getGender().equals("m"))
            {
                genderView.setImageResource(R.drawable.male);
            }
            else
            {
                genderView.setImageResource(R.drawable.female);
            }
            fullNameView.setText(fullName);
            relationView.setText(relationship);



            familyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PersonActivity.this, "New person Activity!!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), PersonActivity.class).putExtra("PERSONID", relations.get(childPosition).getValue()));
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
