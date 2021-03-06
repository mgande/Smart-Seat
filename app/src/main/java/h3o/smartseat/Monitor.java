package h3o.smartseat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import h3o.smartseat.Monitor_Layout.PressureMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Monitor_Custom_Layout#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Monitor extends Fragment {

    public Monitor() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment Monitor.
     */
    // TODO: Rename and change types and number of parameters
    public static Monitor newInstance() {
        return new Monitor();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_monitor, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button btn = (Button) getView().findViewById(R.id.textView);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PressureMap pm = (PressureMap) getView().findViewById(R.id.record_pressure_map);
                pm.setPressure(1, 1, 1, 1, 1);
            }
        });
    }
}
