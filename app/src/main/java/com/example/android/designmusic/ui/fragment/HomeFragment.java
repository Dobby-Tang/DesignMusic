package com.example.android.designmusic.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.designmusic.R;
import com.example.android.designmusic.task.LoadingMusicTask;
import com.example.android.designmusic.ui.adapter.AlbumListAdapter;
import com.example.android.designmusic.ui.adapter.ArtistListAdapter;
import com.example.android.designmusic.ui.adapter.SongListAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class HomeFragment extends Fragment {

    public static final String TYPE_SONG = "music";      //音乐队列
    public static final String TYPE_ARTIST = "artist";    //艺术家
    public static final String TYPE_ALBUM = "album";      //专辑

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LIST_TYPE = "list_type";      //队列类型

    // TODO: Rename and change types of parameters
    private String mType;
    private Context context;
    public static SongListAdapter songListAdapter;
    public static ArtistListAdapter artistListAdapter;
    public static AlbumListAdapter albumListAdapter;

    private OnFragmentInteractionListener mListener;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param Type .
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String Type) {
        Bundle args = new Bundle();
        args.putString(LIST_TYPE,Type);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public HomeFragment() {
        context = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arg = getArguments();
        if (arg != null) {
            mType = getArguments().getString(LIST_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView mHomeList = (RecyclerView) inflater.inflate(R.layout.fragment_home_list,container,false);
        setupHomeList(mType,mHomeList);
        return mHomeList;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private void setupHomeList(String mType,RecyclerView mHomeList){
        LoadingMusicTask musicTask;
        switch (mType){
            case TYPE_SONG:
                if(songListAdapter == null){
                    songListAdapter = new SongListAdapter(null);
                }
                mHomeList.setLayoutManager(new LinearLayoutManager(context));
                mHomeList.setAdapter(songListAdapter);
                break;
            case TYPE_ARTIST:
                if (artistListAdapter == null){
                    artistListAdapter = new ArtistListAdapter(null);
                }
                mHomeList.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));
                mHomeList.setAdapter(artistListAdapter);
                break;
            case TYPE_ALBUM:
                if(albumListAdapter == null){
                    albumListAdapter = new AlbumListAdapter(null);
                }
                mHomeList.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));
                mHomeList.setAdapter(albumListAdapter);
                break;
        }

        musicTask = new LoadingMusicTask(mType,getActivity());
        musicTask.execute();


    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
