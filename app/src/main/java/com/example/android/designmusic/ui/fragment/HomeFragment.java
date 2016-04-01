package com.example.android.designmusic.ui.fragment;

import android.content.Intent;
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
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.task.LoadingMusicTask;
import com.example.android.designmusic.ui.activity.AlbumSongActivity;
import com.example.android.designmusic.ui.activity.ArtistSongActivity;
import com.example.android.designmusic.ui.activity.MusicPlayerActivity;
import com.example.android.designmusic.ui.adapter.AlbumListAdapter;
import com.example.android.designmusic.ui.adapter.ArtistListAdapter;
import com.example.android.designmusic.ui.adapter.BaseListAdapter;
import com.example.android.designmusic.ui.adapter.SongListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class HomeFragment extends Fragment{

    public static final String TAG = "HomeFragment";

    public static final String TYPE_SONG = "music";      //音乐队列
    public static final String TYPE_ARTIST = "artist";    //艺术家
    public static final String TYPE_ALBUM = "album";      //专辑

    public static final String PLAYIONG_LIST = "Playing_list";             //所有本地歌曲
    public static final String PLAYIONG_POSITION = "Playing_position";     //播放歌曲序号
    public static final String SONG_LIST = "album_song_list" ;        //专辑歌曲列表

//    public static final String


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LIST_TYPE = "list_type";      //队列类型

    // TODO: Rename and change types of parameters
    private String mType;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
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
                    songListAdapter = new SongListAdapter();
                }
                songListAdapter.setOnItemClickListener(
                        new BaseListAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position, Object data) {
                                Intent intent = new Intent(getActivity(), MusicPlayerActivity.class);
                                intent.putExtra(HomeFragment.PLAYIONG_POSITION,position);
                                intent.putExtra(HomeFragment.PLAYIONG_LIST
                                        ,(ArrayList<Song>) songListAdapter.getData());
                                getActivity().startActivity(intent);
                            }
                        });
                mHomeList.setLayoutManager(new LinearLayoutManager(getActivity()));
                mHomeList.setAdapter(songListAdapter);
                break;
            case TYPE_ARTIST:
                if (artistListAdapter == null){
                    artistListAdapter = new ArtistListAdapter();
                }

                artistListAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, Object data) {
                        HashMap<String,String> artist = (HashMap<String,String>) data;
                        Intent intent = new Intent(getActivity(), ArtistSongActivity.class);
                        intent.putExtra(LoadingMusicTask.artistId
                                ,artist.get(LoadingMusicTask.artistId));
                        if (songListAdapter != null){
                            ArrayList<Song> songList = songListAdapter.getData();
                            intent.putExtra(SONG_LIST,songList);
                        }
                        getActivity().startActivity(intent);
                    }
                });

                mHomeList.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));
                mHomeList.setAdapter(artistListAdapter);
                break;
            case TYPE_ALBUM:
                if(albumListAdapter == null){
                    albumListAdapter = new AlbumListAdapter();
                }
                albumListAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, Object data) {
                        HashMap<String,String> album = (HashMap<String,String>) data;
                                Intent intent = new Intent(getActivity(), AlbumSongActivity.class);
                        intent.putExtra(LoadingMusicTask.albumId
                                , album.get(LoadingMusicTask.albumId));
                        intent.putExtra(LoadingMusicTask.albumName
                                ,album.get(LoadingMusicTask.albumName));
                        intent.putExtra(LoadingMusicTask.artistName
                                ,album.get(LoadingMusicTask.artistName));
                        intent.putExtra(LoadingMusicTask.songNumber
                                ,album.get(LoadingMusicTask.songNumber));
                        if (songListAdapter != null){
                            ArrayList<Song> songList = songListAdapter.getData();
                            intent.putExtra(SONG_LIST,songList);
                        }
                        getActivity().startActivity(intent);
                    }

                });
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
