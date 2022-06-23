package com.dev175.liveearthmap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.databinding.FragmentVoiceNavigationBinding;
import com.dev175.liveearthmap.utils.Constants;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class VoiceNavigationFragment extends Fragment {

    private static final String TAG = "VoiceNavigationFragment";
    private FragmentVoiceNavigationBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVoiceNavigationBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        //Change Text of toolbar
        Objects.requireNonNull(getActivity()).setTitle(Constants.VOICE_NAVIGATION);

        //Touch on microphone to speak
        binding.microphoneImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSpeechInput();
            }
        });

        return view;
    }

    /////////////////////// Start Speech Input Intent ///////////////////////////////////////////
    private void getSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        //Check that our android has relevant activity for  this implicit intent
        if (intent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager())!=null)
        {
            startActivityForResult(intent,107);
        }
        else
        {
            Toast.makeText(getContext(), "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }

    }

    /////////////////////// On Speech Result ///////////////////////////////////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 107) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Toast.makeText(getContext(), "" + result.get(0), Toast.LENGTH_SHORT).show();

                addVoiceNavigationDisplayFragment(result);
            }
        }
    }

    //////// AddVoiceNavigationDisplayFragment: Passing Speech data in Arraylist<String> ///////
    private void addVoiceNavigationDisplayFragment(ArrayList<String> result) {

        VoiceNavDisplayFragment fragment = new VoiceNavDisplayFragment();

        if (fragment!=null)
        {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container,fragment,"VoiceNavDisplayF");
            transaction.addToBackStack("addVoiceNavDisplayF");
            fragment.setData(result);
            transaction.commit();
        }
        else {
            Log.d(TAG, "VoiceNavDisplayF: not Found!!");
        }

    }

    ////////////////////////////////// Unbinding //////////////////////////////////////////////
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

