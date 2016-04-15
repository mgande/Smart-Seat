package h3o.smartseat;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Record#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Record extends Fragment {

    private ViewSwitcher switcher;
    //Data about user
    private String feet = "";
    private String inches = "";
    private String weight = "";
    private String gender = "";
    private int curr_position = 1;

    private EditText mFtInput;
    private EditText mInInput;
    private EditText mLbInput;
    private Button mSubmitBtn;
    private Button mBackBtn;
    private Button mNextBtn;
    private TextView mPosName;

    private boolean running = false;
    private double[] chairData;

    public Record() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Record.
     */
    // TODO: Rename and change types and number of parameters
    public static Record newInstance() {
        return new Record();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        switcher = (ViewSwitcher) view.findViewById(R.id.record_switcher);

        mFtInput = (EditText) getView().findViewById(R.id.record_ft_value);
        mInInput = (EditText) getView().findViewById(R.id.record_in_value);
        mLbInput = (EditText) getView().findViewById(R.id.record_lb_value);
        mSubmitBtn = (Button) getView().findViewById(R.id.record_submit_form);
        mBackBtn = (Button) view.findViewById(R.id.record_back_btn);
        mNextBtn = (Button) view.findViewById(R.id.record_next_btn);
        mPosName = (TextView) getView().findViewById(R.id.record_position_name);

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!running) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                            "Please connect to the chair.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                feet = mFtInput.getText().toString();
                inches = mInInput.getText().toString();
                weight = mLbInput.getText().toString();

                RadioGroup rg = (RadioGroup) getView().findViewById(R.id.record_gender_radio_group);

                switch (rg.getCheckedRadioButtonId()) {
                    case R.id.record_radio_female:
                        gender = "female";
                        break;
                    case R.id.record_radio_male:
                        gender = "male";
                        break;
                    case R.id.record_radio_other:
                        gender = "other";
                        break;
                    default:
                        gender = "";
                }

                Log.d("data", feet + "," + inches + "," + weight + "," + gender);
                if(inches.isEmpty() || feet.isEmpty() || weight.isEmpty() || gender.isEmpty()) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                            "Required field is empty.", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(
                            getActivity().getCurrentFocus().getWindowToken(), 0);
                    switcher.showNext();
                }
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curr_position = 1;
                mPosName.setText("Position 1");

                switcher.showPrevious();
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curr_position != 9) {
                    curr_position++;
                    mPosName.setText("Position " + curr_position);
                } else {
                    switcher.showNext();
                    mFtInput.setText("");
                    mInInput.setText("");
                    mLbInput.setText("");
                    RadioGroup tmp = (RadioGroup) getView().findViewById(R.id.record_gender_radio_group);
                    tmp.clearCheck();
                    switcher.requestFocus();
                    curr_position = 1;

                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                            "Thanks for testing the chair!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    public void updateChairData(boolean r, double[] data) {
        running = r;
        chairData = data;
    }
}