package nl.mprog.nao_pilot;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * NAO Pilot
 * Caitlin Lagrand (10759972)
 * UvA Programmeerproject
 *
 * The walk fragment of the app.
 * Let the robot walk.
 */

public class WalkFragment extends Fragment {

    View view;
    NetworkThread networkThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_walk, container, false);
        networkThread = NetworkThread.getInstance();
        handleSeekbars();
        handleButtons();
        return view;
    }

    private void handleSeekbars() {
        SeekBar speedBar = (SeekBar) view.findViewById(R.id.seekBarSpeedWalk);
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                TextView speedText = (TextView) getActivity().findViewById(R.id.speedTextWalk);
                speedText.setText("Speed: " + String.valueOf(progress) + " %");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    private void handleButtons() {
        ImageButton up = (ImageButton) view.findViewById(R.id.upButton);
        up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Walk with speed
                    if (networkThread != null) {
                        String speedText = (String) ((TextView) view.findViewById(R.id.speedTextWalk)).getText();
                        String speed = speedText.split("\\s+")[1];
                        float xSpeed = Float.parseFloat(speed) / 100;
                        JSONObject json = new JSONObject();
                        try {
                            json.put("type", "walk");
                            json.put("x_speed", xSpeed);
                            json.put("y_speed", 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        networkThread.sendMessage(json);
                    }
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Stop walking
                    JSONObject json = new JSONObject();
                    try {
                        json.put("type", "walk");
                        json.put("x_speed", 0);
                        json.put("y_speed", 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    networkThread.sendMessage(json);
                    return true;
                }
                return false;
            }
        });
        ImageButton down = (ImageButton) view.findViewById(R.id.downButton);
        down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Walk with speed
                    if (networkThread != null) {
                        String speedText = (String) ((TextView) view.findViewById(R.id.speedTextWalk)).getText();
                        String speed = speedText.split("\\s+")[1];
                        float xSpeed = Float.parseFloat(speed) / 100;
                        JSONObject json = new JSONObject();
                        try {
                            json.put("type", "walk");
                            json.put("x_speed", -xSpeed);
                            json.put("y_speed", 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        networkThread.sendMessage(json);
                    }
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Stop walking
                    JSONObject json = new JSONObject();
                    try {
                        json.put("type", "walk");
                        json.put("x_speed", 0);
                        json.put("y_speed", 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    networkThread.sendMessage(json);
                    return true;
                }
                return false;
            }
        });

        ImageButton left = (ImageButton) view.findViewById(R.id.leftButton);
        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Walk with speed
                    if (networkThread != null) {
                        String speedText = (String) ((TextView) view.findViewById(R.id.speedTextWalk)).getText();
                        String speed = speedText.split("\\s+")[1];
                        float ySpeed = Float.parseFloat(speed) / 100;
                        JSONObject json = new JSONObject();
                        try {
                            json.put("type", "walk");
                            json.put("x_speed", 0);
                            json.put("y_speed", ySpeed);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        networkThread.sendMessage(json);
                    }
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Stop walking
                    JSONObject json = new JSONObject();
                    try {
                        json.put("type", "walk");
                        json.put("x_speed", 0);
                        json.put("y_speed", 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    networkThread.sendMessage(json);
                    return true;
                }
                return false;
            }
        });

        ImageButton right = (ImageButton) view.findViewById(R.id.rightButton);
        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Walk with speed
                    if (networkThread != null) {
                        String speedText = (String) ((TextView) view.findViewById(R.id.speedTextWalk)).getText();
                        String speed = speedText.split("\\s+")[1];
                        float ySpeed = Float.parseFloat(speed) / 100;
                        JSONObject json = new JSONObject();
                        try {
                            json.put("type", "walk");
                            json.put("x_speed", 0);
                            json.put("y_speed", -ySpeed);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        networkThread.sendMessage(json);
                    }
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Stop walking
                    JSONObject json = new JSONObject();
                    try {
                        json.put("type", "walk");
                        json.put("x_speed", 0);
                        json.put("y_speed", 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    networkThread.sendMessage(json);
                    return true;
                }
                return false;
            }
        });


        ImageButton leftTurn = (ImageButton) view.findViewById(R.id.leftTurnButton);
        leftTurn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Walk with speed
                    if (networkThread != null) {
                        String speedText = (String) ((TextView) view.findViewById(R.id.speedTextWalk)).getText();
                        float speed = Float.parseFloat(speedText.split("\\s+")[1]) / 100;
                        JSONObject json = new JSONObject();
                        try {
                            json.put("type", "turn");
                            json.put("speed", speed);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        networkThread.sendMessage(json);
                    }
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Stop walking
                    JSONObject json = new JSONObject();
                    try {
                        json.put("type", "walk");
                        json.put("x_speed", 0);
                        json.put("y_speed", 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    networkThread.sendMessage(json);
                    return true;
                }
                return false;
            }
        });

        ImageButton rightTurn = (ImageButton) view.findViewById(R.id.rightTurnButton);
        rightTurn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Walk with speed
                    if (networkThread != null) {
                        String speedText = (String) ((TextView) view.findViewById(R.id.speedTextWalk)).getText();
                        float speed = Float.parseFloat(speedText.split("\\s+")[1]) / 100;
                        JSONObject json = new JSONObject();
                        try {
                            json.put("type", "turn");
                            json.put("speed", -speed);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        networkThread.sendMessage(json);
                    }
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Stop walking
                    JSONObject json = new JSONObject();
                    try {
                        json.put("type", "walk");
                        json.put("x_speed", 0);
                        json.put("y_speed", 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    networkThread.sendMessage(json);
                    return true;
                }
                return false;
            }
        });

    }

}
