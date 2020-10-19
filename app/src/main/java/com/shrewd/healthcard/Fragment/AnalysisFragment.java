package com.shrewd.healthcard.Fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.shrewd.healthcard.Adapter.AnalysisAdapter;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.databinding.FragmentAnalysisBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnalysisFragment extends Fragment {


    private static final String TAG = "AnalysisFragment";
    private Context mContext;
    private int clientTextColor;
    private String clientMessage;
    private ClientThread clientThread;
    private String message;
    private Thread thread;
    private PieChart pcPatient;
    private ArrayList pcPatientpieEntries;
    private PieDataSet pcPatientpieDataSet;
    private PieData pcPatientpieData;
    private LineChart lcPatient;
    private LineDataSet lcPatientlineDataSet;
    private LineData lcPatientlineData;
    private ArrayList lcPatientlineEntries;
    private BarChart bcPatient;
    private FragmentAnalysisBinding binding;

    public AnalysisFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAnalysisBinding.inflate(getLayoutInflater(), container, false);
        mContext = getContext();
        clientTextColor = mContext.getColor(R.color.colorAccent);
//        pcPatient = view.findViewById(R.id.pcPatient);
//        pcPatient.setCenterText("Disease");
//        pcPatient.setCenterTextSize(getResources().getDimension(R.dimen._7sdp));
//        pcPatient.getDescription().setEnabled(false);

        AnalysisAdapter analysisFragment = new AnalysisAdapter(mContext);
        binding.rvAnalysis.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        binding.rvAnalysis.setAdapter(analysisFragment);

//        lcPatient = view.findViewById(R.id.lcPatient);
//        bcPatient = view.findViewById(R.id.bcPatient);

        /*FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("AWS")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Log.e(TAG, "onSuccess: " + doc.getString(CS.host) + " " + doc.getLong(CS.port));
                            clientThread = new ClientThread(doc.getString(CS.host), doc.getLong(CS.port), 1);
                            thread = new Thread(clientThread);
                            thread.start();
                            break;
                        }
                    }
                });

        pcPatient.getDescription().setEnabled(false);
        pcPatient.setCenterText("Disease");
        pcPatient.setCenterTextSize(getResources().getDimension(R.dimen._7sdp));
        pcPatient.getDescription().setEnabled(false);
        db.collection(CS.History)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        HashMap<String, Float> map = new HashMap<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            if (map.containsKey(doc.getString(CS.disease))) {
                                try {
                                    map.put(doc.getString(CS.disease), map.get(doc.getString(CS.disease)) + 1);
                                } catch (Exception ex) {
                                    Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                }
                            } else {
                                map.put(doc.getString(CS.disease), 1f);
                            }
                        }

                        Set<String> set = map.keySet();

                        Log.e(TAG, "onSuccess: ***");
                        pcPatientpieEntries = new ArrayList<>();
                        for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
                            String s = it.next();
                            Log.e(TAG, "onSuccess: " + s);
                            Log.e(TAG, "onSuccess: " + map.get(s));
                            pcPatientpieEntries.add(new PieEntry(map.get(s), s));
                        }

                        if (pcPatientpieEntries.size() > 0) {
                            pcPatientpieDataSet = new PieDataSet(pcPatientpieEntries, "");
                            pcPatientpieData = new PieData(pcPatientpieDataSet);
                            pcPatient.setData(pcPatientpieData);
                            pcPatientpieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                            pcPatientpieDataSet.setSliceSpace(2f);
                            pcPatientpieDataSet.setValueTextColor(Color.WHITE);
                            pcPatientpieDataSet.setValueTextSize(10f);
                            pcPatientpieDataSet.setSliceSpace(5f);
                            pcPatient.animateXY(1500, 500);
                            Log.e(TAG, "onSuccess: ***");
                        }
                    }
                });*/
        return binding.getRoot();
    }

    class ClientThread implements Runnable {

        private final String host;
        private final Long port;
        private final int type;
        private Socket socket;
        private BufferedReader input;

        public ClientThread(String host, Long port, int type) {
            this.host = host;
            this.port = port;
            this.type = type;
        }

        @Override
        public void run() {

            try {

                InetAddress serverAddr = InetAddress.getByName(host);
                showMessage("Connecting to Server...", clientTextColor, true);

                socket = new Socket(serverAddr, port.intValue());

                if (socket.isBound()) {
                    showMessage("Connected to Server...", clientTextColor, true);
                }

                /*String str = type + "#21 10.23 45 0.25" +
                        "22 11.23 46 0.50" +
                        "23 12.23 47 0.75" +
                        "24 13.23 48 1.00" +
                        "25 14.23 49 1.25" +
                        "26 15.23 40 0.50" +
                        "27 16.23 41 0.75" +
                        "28 17.23 42 2.00" +
                        "29 18.23 43 2.25";*/
                clientMessage = type + "";
                showMessage(clientMessage, Color.BLUE, false);
                if (null != clientThread) {
                    if (clientMessage.length() > 0) {
                        clientThread.sendMessage(clientMessage);
                    }
//            edMessage.setText("");
                } else {
                    Log.e(TAG, "run: client thread is null");
                }


                while (!Thread.currentThread().isInterrupted()) {

                    this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    message = input.readLine();

                    if (message != null && !message.equals("Server Disconnected...")) {
                        Log.e(TAG, "run: " + message);
                        ((Activity) mContext).runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                String[] alData = message.split(" ");
                                /*for (int i = 0; i < alData.size(); ++i) {
                                    Toast.makeText(mContext, "" + alData.get(i), Toast.LENGTH_SHORT).show();
                                }*/
//                                lcPatientlineEntries = new ArrayList();
                                List<BarEntry> lineEntries = new ArrayList<>();

                                for (int i = 0; i < alData.length; i++) {
//                                    Log.e(TAG, "run: " + Float.parseFloat(String.valueOf(alData[i])));
                                    try {
//                                        lcPatientlineEntries.add(new Entry(i+10,i));
                                        Log.e(TAG, "run: " + alData[i]);
                                        lineEntries.add(new BarEntry(Float.valueOf(alData[i]), (int) i));
                                    } catch (Exception ex) {
                                        Log.e(TAG, "run: " + ex.getMessage());
                                    }
                                }

//                                lineEntries.add(new BarEntry(0f, 0));
                                /*BarDataSet set1 = new BarDataSet(lineEntries, "The year 2017");
                                set1.setColors(ColorTemplate.MATERIAL_COLORS);
                                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                                dataSets.add(set1);

                                BarData data = new BarData(dataSets);

                                data.setValueTextSize(10f);
                                data.setBarWidth(0.9f);

                                bcPatient.setTouchEnabled(false);
                                bcPatient.setData(data);
                                bcPatient.animateY(1500);*/



                                /*for (int i = 0; i < alData.length; i++) {
//                                    Log.e(TAG, "run: " + Float.parseFloat(String.valueOf(alData[i])));
                                    try {
//                                        lcPatientlineEntries.add(new Entry(i+10,i));
                                        Log.e(TAG, "run: " + new Entry(Float.valueOf(alData[i]), i));
                                        lineEntries.add(new Entry(Float.valueOf(alData[i]).floatValue(), i));
                                    } catch (Exception ex) {
                                        Log.e(TAG, "run: " + ex.getMessage());
                                    }
                                }*/
//                                lineEntries.add(new Entry(2f, 0));

                                /*lineEntries.add(new Entry(2f, 0));
                                lineEntries.add(new Entry(4f, 1));
                                lineEntries.add(new Entry(6f, 1));
                                lineEntries.add(new Entry(8f, 3));
                                lineEntries.add(new Entry(7f, 4));
                                lineEntries.add(new Entry(3f, 3));*/

                                /*lcPatientlineDataSet = new LineDataSet(lineEntries, "");
                                lcPatientlineData = new LineData(lcPatientlineDataSet);
                                lcPatient.setData(lcPatientlineData);
                                lcPatientlineDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                                lcPatientlineDataSet.setValueTextColor(Color.BLACK);
                                lcPatientlineDataSet.setValueTextSize(18f);
                                lcPatient.animateY(1500);*/

                            }
                        });
                    }
                    if (null == message || "Disconnect".contentEquals(message)) {
                        Thread.interrupted();
                        message = "Server Disconnected...";
                        showMessage(message, Color.RED, false);
                        break;
                    }
                    showMessage("Server: " + message, clientTextColor, true);

                }

            } catch (NullPointerException e3) {
                showMessage("error returned", Color.RED, true);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        void sendMessage(final String message) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (null != socket) {
                            /*PrintWriter out = new PrintWriter(new BufferedWriter(
                                    new OutputStreamWriter(socket.getOutputStream())),
                                    true);*/
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                            out.println(message);

                        }
                    } catch (Exception e) {
                        Log.e(TAG, "run: error: " + e.getMessage());
                    }
                }
            }).start();
        }

        public void showMessage(final String message, final int color, final Boolean value) {
        /*handler.post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run: " + message);
//                msgList.addView(textView(message, color, value));
            }
        });*/
            Log.e(TAG, "showMessage: " + message);


        }

    }

}
