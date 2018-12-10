package cn.cjwddz.knowu;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import cn.cjwddz.knowu.activity.MainActivity;
import cn.cjwddz.knowu.service.KnowUBleService;
import cn.cjwddz.knowu.service.Protocol;
import cn.cjwddz.knowu.service.ServiceInterface;
import cn.cjwddz.knowu.view.CountDownTimerView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentUI.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentUI#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentUI extends Fragment implements CountDownTimerView.onTimer{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String FRAGMENT_ID = "fragment_id";
    private  int id = 0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private  View fragmentView;
    public String fragment_ui;
    private Button button;
    private Button startTiming;
    private OnFragmentInteractionListener mListener;
    private CountDownTimerView countDownTimerView;

    private boolean timerStatus = true;
    private int times;
    private Button st_button;

    ServiceInterface serviceInterface;
   private KnowUBleService kUBService;

    public FragmentUI() {
        // Required empty public constructor
    }
    public void setBleService(KnowUBleService service){
        kUBService=service;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentUI.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentUI newInstance(String param1, String param2) {
        FragmentUI fragment = new FragmentUI();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
           // mParam2 = getArguments().getString(ARG_PARAM2);
            id = getArguments().getInt(FRAGMENT_ID);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         fragmentView = inflater.inflate(R.layout.fragment_unconnect, container, false);
        button = fragmentView.findViewById(R.id.connect_bt);
       // View fragmentView1 = inflater.inflate(R.layout.fragment_connecting, container, false);
       // View fragmentView2 = inflater.inflate(R.layout.fragment_connect_success, container, false);
        //View fragmentView3 = inflater.inflate(R.layout.fragment_connected, container, false);

        switch(id){
            case 1:
                fragmentView = inflater.inflate(R.layout.fragment_connecting, container, false);
                ImageView imageView = fragmentView.findViewById(R.id.smile);
               final Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.smile);

                break;
            case 2:
                fragmentView = inflater.inflate(R.layout.fragment_connect_success, container, false);
                break;
            case 3:
                initFragment3(inflater,container);
                break;
            default:
                //fragmentView = inflater.inflate(R.layout.fragment_unconnect, container, false);
                break;
        }
        // Inflate the layout for this fragment
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity) getActivity();
                activity.connect();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void countdownStart(int second) {
        kUBService.sendMessage(Protocol.getOpenInstruct());
        if (mListener != null) {
            mListener.countdownStart();
        }
       // st_button.setText("停 止");
        //st_button.setTextSize(20);
    }

    @Override
    public void countdownFinished(int status) {
        kUBService.sendMessage(Protocol.getCloseInstruct());
        if (mListener != null) {
            mListener.countdownFinished();
        }
        st_button.setText("开 始");
        st_button.setTextSize(20);
    }

    @Override
    public void countdownStop() {
        kUBService.sendMessage(Protocol.getCloseInstruct());
        if (mListener != null) {
            mListener.countdownStop();
        }
       // st_button.setText("开 始");
        //st_button.setTextSize(20);
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
        void countdownStart();
        void countdownStop();
        void countdownFinished();
    }

    private void initFragment3(LayoutInflater inflater,ViewGroup container){
        fragmentView = inflater.inflate(R.layout.fragment_connected, container, false);
        countDownTimerView =  fragmentView.findViewById(R.id.timer);
        countDownTimerView.setTimer(this);
        st_button = fragmentView.findViewById(R.id.startTiming);
        st_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(countDownTimerView.isZero() != 0){
                    if(!st_button.getText().toString().equals("开 始")){
                        countDownTimerView.stop();
                        st_button.setText("开 始");
                        st_button.setTextSize(20);
                    }else{
                        countDownTimerView.start();
                        st_button.setText("停 止");
                        st_button.setTextSize(20);
                    }
                }
            }
        });
        st_button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                kUBService.disConnectDevice();
                return true;
            }
        });
    }
}
