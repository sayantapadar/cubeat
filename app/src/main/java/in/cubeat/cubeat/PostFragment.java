package in.cubeat.cubeat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {

    ArrayList<PostPreview> data=new ArrayList<>();
    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment getInstance(ArrayList<PostPreview> data){
        PostFragment fragment=new PostFragment();
        fragment.data=data;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout= inflater.inflate(R.layout.fragment_post, container, false);
        RecyclerView recyclerView= (RecyclerView) layout.findViewById(R.id.post_recycler);
        recyclerView.setAdapter(new PostRecyclerAdapter(getContext(),data));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        return layout;
    }

}
