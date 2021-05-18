package ph.edu.dlsu.codehub;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity{

    //Note: I used activity since it is hard  to convert this into a fragment (a hassle)
    private Intent intent;
    private String inputString;
    private RecyclerView searchResults;

    private DatabaseReference usersDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        //init ids
        searchResults = (RecyclerView) findViewById(R.id.recyclerView);
        searchResults.setHasFixedSize(true);
        searchResults.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //init firebase
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        //get information from intent
        intent = getIntent();
        inputString = intent.getStringExtra("stringQuery");

        //top banner
        Toolbar myToolbar = (Toolbar) findViewById(R.id.top_banner);
        setSupportActionBar(myToolbar);

        //search
        searchPeopleWithFriends(inputString);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.top_bar, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                inputString = s;
                searchPeopleWithFriends(inputString);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        CircleImageView profilePic;
        TextView name, status;

        public FindFriendsViewHolder(View itemView)
        {
            super(itemView);

            mView = itemView;
            name = (TextView) mView.findViewById(R.id.user_display_profile_name);
            profilePic = (CircleImageView) mView.findViewById(R.id.user_display_profile_image);
            status = (TextView) mView.findViewById(R.id.user_display_user_status);
            this.mView = itemView;
        }

        public void setProfilePicture(String profilePicture) {
            if(profilePicture != null)
            {
                Log.d("DEBUGGING", profilePicture + "");
//                Uri uri = Uri.parse(profilePicture);

                Picasso.get()
                        .load(profilePicture)
                        .placeholder(R.drawable.ic_baseline_event_seat_24)
                        .into(profilePic);
            }
//



            //TODO: Read https://stackoverflow.com/questions/36045522/android-picasso-image-does-not-load
//            Picasso.get().load(R.drawable.ic_baseline_person_24).into(profilePic);
            //need to make sure argument is not a null pointer
//            Picasso.get().load(profilePicture).placeholder.into(profilePic);
        }

        public void setName(String fullName) {
            name.setText(fullName);
        }

        public void setStatus(String statusText) {
            status.setText(statusText);
        }
    }

    private void searchPeopleWithFriends(String query) {

        query = query.toLowerCase();
        //TODO: make results onclickable (felix na)
        //should display a list of all users since the order has been omitted
        Query searchPeopleWithFriendsQuery = usersDatabaseReference.orderByChild("fullNameInLowerCase" ).startAt(query).endAt(query + "\uf8ff");


        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(searchPeopleWithFriendsQuery, User.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<User, FindFriendsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull @NotNull FindFriendsViewHolder holder, int position, @NonNull @NotNull User model) {
                String name = model.getFullName();
                String pos = getRef(position).getKey();
                holder.setName(name);
                holder.setProfilePicture(model.getprofileImageLink());
                holder.setStatus(model.getStatus());
                Log.d("Debug Name: ", name);
//                Log.d("Debug Profile Picture: ", model.getProfilePicture());

                holder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(SearchActivity.this, ViewOtherProfileActivity.class);
                    intent.putExtra("Position", pos);
                    startActivity(intent);
                });

            }

            @Override
            public FindFriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_display_layout, parent, false);

                return new FindFriendsViewHolder(view);
            }

        };
        searchResults.setAdapter(adapter);
        adapter.startListening();
    }
}
