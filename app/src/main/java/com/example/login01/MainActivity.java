package com.example.login01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.login01.model.Persona;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private List<Persona> listaPersona = new ArrayList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPersona;
    EditText nomP,apeP,correoP,contraP;
    ListView listaP;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Persona personaSeleccionada;
    public static final Pattern EMAIL_ADDRESS = Pattern.compile("^[\\w\\\\\\+]+(\\.[\\w\\\\]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z](2,4)$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nomP = findViewById(R.id.etNombre);
        apeP = findViewById(R.id.etApellido);
        correoP = findViewById(R.id.etCorreo);
        contraP = findViewById(R.id.etContraseña);

        listaP = findViewById(R.id.lvDatosPersonas);
        inicializarFirebase();
        listarDatos();
        listaP.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                personaSeleccionada = (Persona) adapterView.getItemAtPosition(i);
                nomP.setText(personaSeleccionada.getNombre());
                apeP.setText(personaSeleccionada.getApellido());
                correoP.setText(personaSeleccionada.getCorreo());
                contraP.setText(personaSeleccionada.getContraseña());

            }
        });
    }

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaPersona.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    Persona p = objSnapshot.getValue(Persona.class);
                    listaPersona.add(p);

                    arrayAdapterPersona = new ArrayAdapter<Persona>(MainActivity.this, android.R.layout.simple_list_item_1, listaPersona);
                    listaP.setAdapter(arrayAdapterPersona);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        String nombre = nomP.getText().toString();
        String apellido = apeP.getText().toString();
        String correo = correoP.getText().toString();
        String contraseña = contraP.getText().toString();

        switch (item.getItemId())
        {
            case R.id.ic_add:
                if (nombre.equals("")||apellido.equals("")||correo.equals("")||contraseña.equals("")){
                    Toast.makeText(this, "Para agregar, ingrese los campos requeridos", Toast.LENGTH_SHORT).show();
                    validacion();
                }
                else {
                    Persona p = new Persona();

                    if (!correo.isEmpty()){
                        if (!validarEmail(correo)){
                            correoP.setError("Formato inválido");
                            Toast.makeText(this, "Ingrese un correo con formato válido, no se pudo registrar", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(this, "Formato email válido", Toast.LENGTH_SHORT).show();
                            p.setUid(UUID.randomUUID().toString());
                            p.setNombre(nombre);
                            p.setApellido(apellido);
                            p.setCorreo(correo);
                            p.setContraseña(contraseña);
                            databaseReference.child("Persona").child(p.getUid()).setValue(p);
                            Toast.makeText(this, "Datos agregados correctamente", Toast.LENGTH_LONG).show();
                            limpiarCajas();
                        }
                    }
                }
                break;

            case R.id.ic_save:
                if (nombre.equals("")||apellido.equals("")||correo.equals("")||contraseña.equals("")){
                    Toast.makeText(this, "Seleccione persona agregada en la lista, para editar", Toast.LENGTH_SHORT).show();
                }
                else {
                    Persona p = new Persona();
                    p.setUid(personaSeleccionada.getUid());
                    p.setNombre(nomP.getText().toString().trim());
                    p.setApellido(apeP.getText().toString().trim());
                    p.setCorreo(correoP.getText().toString().trim());
                    p.setContraseña(contraP.getText().toString().trim());
                    databaseReference.child("Persona").child(p.getUid()).setValue(p);
                    Toast.makeText(this, "Actualizado", Toast.LENGTH_LONG).show();
                    limpiarCajas();
                }
                break;

            case R.id.ic_delete:
                if (nombre.equals("")||apellido.equals("")||correo.equals("")||contraseña.equals("")){
                    Toast.makeText(this, "Seleccione persona agregada en la lista, para eliminarla", Toast.LENGTH_SHORT).show();
                }
                else{
                Persona pe = new Persona();
                pe.setUid(personaSeleccionada.getUid());
                databaseReference.child("Persona").child(pe.getUid()).removeValue();
                Toast.makeText(this,"Eliminado",Toast.LENGTH_LONG).show();
                limpiarCajas();
                }
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void limpiarCajas() {
        nomP.setText("");
        apeP.setText("");
        correoP.setText("");
        contraP.setText("");
    }

    private void validacion() {
        String nombre = nomP.getText().toString();
        String apellido = apeP.getText().toString();
        String correo = correoP.getText().toString();
        String contraseña = contraP.getText().toString();

        if (nombre.equals(""))
        {
            nomP.setError("Campo requerido");
        }
        else if (apellido.equals(""))
        {
            apeP.setError("Campo requerido");
        }
        else if (correo.equals("")){
            correoP.setError("Campo requerido");
        }
        else if (contraseña.equals("")){
            contraP.setError("Campo requerido");
        }
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();

    }
}
