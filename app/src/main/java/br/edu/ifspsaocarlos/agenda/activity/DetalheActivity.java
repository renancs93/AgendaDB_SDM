package br.edu.ifspsaocarlos.agenda.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Calendar;

import br.edu.ifspsaocarlos.agenda.data.ContatoDAO;
import br.edu.ifspsaocarlos.agenda.model.Contato;
import br.edu.ifspsaocarlos.agenda.R;


public class DetalheActivity extends AppCompatActivity implements View.OnClickListener {
    private Contato c;
    private ContatoDAO cDAO;

    private EditText nameText, foneText, emailText, dateText;
    private ImageButton imageButtonCalendar, imageButtonAddFone;
    private TextView textViewBirthday;

    private LinearLayout telefoneLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textViewBirthday = (TextView)findViewById(R.id.textViewBirthday);
        textViewBirthday.setText(getString(R.string.aniversario) + " " + getString(R.string.dia_mes));

        nameText = (EditText)findViewById(R.id.editTextNome);
        foneText = (EditText)findViewById(R.id.editTextFone);

        emailText = (EditText)findViewById(R.id.editTextEmail);
        dateText = (EditText)findViewById(R.id.editTexDate);

        imageButtonCalendar = (ImageButton)findViewById(R.id.imageButtonCalendar);
        imageButtonCalendar.setOnClickListener(this);

        imageButtonAddFone = (ImageButton)findViewById(R.id.btnAddFone);
        imageButtonAddFone.setOnClickListener(this);

        telefoneLinearLayout = findViewById(R.id.linearLayoutAddNewPhones);

        if (getIntent().hasExtra("contato"))
        {
            this.c = (Contato) getIntent().getSerializableExtra("contato");

            nameText.setText(c.getNome());

            String multFones[] = c.getFone().split(";");
            if (multFones.length <= 1){
                foneText.setText(multFones[0]);
            }
            else{
                foneText.setText(multFones[0]);
                addNewFone(multFones);
            }

            emailText.setText(c.getEmail());
            dateText.setText(c.getBirthday());

            int fav = c.getFavorite();
            Switch favoriteSwitch = (Switch)findViewById(R.id.switchFavorite);
            favoriteSwitch.setChecked(fav == 1?true:false);

            int pos =c.getNome().indexOf(" ");
            if (pos==-1)
                pos=c.getNome().length();
            setTitle(c.getNome().substring(0,pos));
        }
        cDAO = new ContatoDAO(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detalhe, menu);
        if (!getIntent().hasExtra("contato"))
        {
            MenuItem item = menu.findItem(R.id.delContato);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.salvarContato:
                salvar();
                return true;
            case R.id.delContato:
                apagar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void apagar()
    {
        cDAO.apagaContato(c);

        Intent resultIntent = new Intent();
        setResult(3,resultIntent);
        finish();
    }

    private void salvar()
    {
        if (validarCampos()) {

            String name = nameText.getText().toString();
            String fone = foneText.getText().toString();
            String email = emailText.getText().toString();

            Switch field_favorite = (Switch) findViewById(R.id.switchFavorite);
            String birthday = dateText.getText().toString();

            //Estado favorito
            int favorito = 0;
            if (field_favorite.isChecked()) {
                favorito = 1;
            }

            if (telefoneLinearLayout.getChildCount() > 0){
                fone = getMultFones(fone);
            }

            if (c == null)
                c = new Contato();

            c.setNome(name);
            c.setFone(fone);
            c.setEmail(email);
            c.setFavorite(favorito);
            c.setBirthday(birthday);

            cDAO.salvaContato(c);
            //c.setId(10);
            //ContatoAdapter.Adiciona(c);
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        }
        else {
            Toast.makeText(this, "Campos Obrigatórios: \n"+
                                            getString(R.string.nome)+
                                            "\n"+getString(R.string.fone),
                            Toast.LENGTH_SHORT)
                    .show();
        }

    }

    public boolean validarCampos(){

        boolean ok = true;

        if (nameText.getText().toString().trim().equals("") || foneText.getText().toString().trim().equals("")){
            ok = false;
        }

        return ok;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.imageButtonCalendar:
                openCalendarDialog();
                break;
            case R.id.btnAddFone:
                addNewFone(null);
                break;
            case R.id.imageButtonDelNewFone:
                removeViewNewFone(telefoneLinearLayout);
                break;


        }
    }

    public String getMultFones(String fone){

        String fones = fone;

        for (int i=0 ; i<telefoneLinearLayout.getChildCount() ; i++){

            View view = telefoneLinearLayout.getChildAt(i);

            EditText newFone = view.findViewById(R.id.editTextNewFone);
            fones = fones+";"+newFone.getText().toString();

        }

        return fones;
    }

    private void removeViewNewFone(View view) {

        ViewGroup parent = (ViewGroup) view.getParent();
        parent.removeView(view);

        Toast.makeText(this, "Removido Telefone", Toast.LENGTH_SHORT).show();

    }

    private void addNewFone(String[] fones) {

        LayoutInflater layoutInflater = getLayoutInflater();
        View newFoneLayout = null;
        ImageButton imageButtonDel;

        if (null != fones){

            //para iniciar a partir do primeiro telefone Extra
            for (int i=1 ; i<fones.length ; i++){

                newFoneLayout = layoutInflater.inflate(R.layout.layout_new_fone, null);
                telefoneLinearLayout.addView(newFoneLayout);

                EditText newFone = newFoneLayout.findViewById(R.id.editTextNewFone);
                newFone.setText(fones[i]);

                imageButtonDel = newFoneLayout.findViewById(R.id.imageButtonDelNewFone);
                imageButtonDel.setOnClickListener(this);
            }
        }
        else{
            newFoneLayout = layoutInflater.inflate(R.layout.layout_new_fone, null);
            telefoneLinearLayout.addView(newFoneLayout);

            imageButtonDel = newFoneLayout.findViewById(R.id.imageButtonDelNewFone);
            imageButtonDel.setOnClickListener(this);
        }

    }

    private void openCalendarDialog() {

        // calender class's instance and get current date , month and year from calender
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        DecimalFormat df = new DecimalFormat("00");

                        dateText.setText(df.format(day)
                                        + "/" + (df.format(month + 1)));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();


    }
}

