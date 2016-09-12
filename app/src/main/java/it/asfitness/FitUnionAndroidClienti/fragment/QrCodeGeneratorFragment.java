package it.asfitness.FitUnionAndroidClienti.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import it.asfitness.FitUnionAndroidClienti.R;
import it.asfitness.FitUnionAndroidClienti.data.Clienti;
import it.asfitness.FitUnionAndroidClienti.utils.Contents;
import it.asfitness.FitUnionAndroidClienti.utils.QRCodeEncoder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QrCodeGeneratorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QrCodeGeneratorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QrCodeGeneratorFragment extends Fragment {
    private static final String ARG_PARAM1 = "";
    private static final String ARG_PARAM2 = "";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    TextView txNome;
    TextView txIngressi;
    ImageView imgQr;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types and number of parameters
    public static QrCodeGeneratorFragment newInstance() {
        QrCodeGeneratorFragment fragment = new QrCodeGeneratorFragment();
        return fragment;
    }

    public QrCodeGeneratorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        initQrCode();
        return inflater.inflate(R.layout.fragment_qr_code_generator, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    public void initQrCode(){
        BackendlessUser user = Backendless.UserService.CurrentUser();
        final String objId = user.getObjectId();
        String whereClause = "userId = '"+objId+"'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(Clienti.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Clienti>>() {
            @Override
            public void handleResponse(BackendlessCollection<Clienti> response) {

                txNome = (TextView) getActivity().findViewById(R.id.tx_nome);
                txIngressi = (TextView) getActivity().findViewById(R.id.tx_ingressi);
                imgQr = (ImageView) getActivity().findViewById(R.id.qr_image);

                Clienti cliente = response.getData().get(0);
                txNome.setText(cliente.getNome());
                txIngressi.setText(""+cliente.getIngressi_disponibili());
                //Find screen size
                WindowManager manager = (WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                int width = point.x;
                int height = point.y;
                int smallerDimension = width < height ? width : height;
                smallerDimension = smallerDimension * 3/4;

                //Encode with a QR Code image
                QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(cliente.getObjectId(),
                        null,
                        Contents.Type.TEXT,
                        BarcodeFormat.QR_CODE.toString(),
                        smallerDimension);
                try {
                    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                    imgQr.setImageBitmap(bitmap);

                } catch (WriterException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}
