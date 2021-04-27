package ph.edu.dlsu.codehub.fragmentClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import ph.edu.dlsu.codehub.DeleteAndEditPostActivity;
import ph.edu.dlsu.codehub.LoginActivity;
import ph.edu.dlsu.codehub.Post;
import ph.edu.dlsu.codehub.R;
import ph.edu.dlsu.codehub.RegisterActivity;


public class HomeFragment extends Fragment {
    private DatabaseReference postRef;
    private RecyclerView postList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        postList = view.findViewById(R.id.recyclerView);
        postList.setHasFixedSize(true);
        // Sets the order of posting to reverse (recently posted items show up first)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        viewPosts();
    }

    private void viewPosts() {
        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>().setQuery(postRef, Post.class).build();
        FirebaseRecyclerAdapter<Post, PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Post model) {
                String pos = getRef(position).getKey();
                holder.postDetails.setText(model.getFullName() + " | " + model.getDate() + " | " + model.getTime());
                holder.postBody.setText(model.getBody());
                holder.postTitle.setText(model.getTitle());
                holder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(getActivity(), DeleteAndEditPostActivity.class);
                    intent.putExtra("Position", pos);
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
                return new PostViewHolder(view);
            }
        };
        postList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postDetails, postBody, postTitle;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postDetails = itemView.findViewById(R.id.single_post_details);
            postBody = itemView.findViewById(R.id.single_post_body);
            postTitle = itemView.findViewById(R.id.single_post_title);
        }
    }

}
