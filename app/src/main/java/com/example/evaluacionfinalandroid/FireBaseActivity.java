package com.example.evaluacionfinalandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireBaseActivity extends AppCompatActivity{

    // Declarar varaibles del formulario de cursos
    private EditText txtId, txtNombre, txtDescripcion;
    private Spinner spCategoria, spDuracion, spDificultad;
    private ListView lista;

    // Variable para la conexion a la base de datos
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
        cargarListaFireStore();   // Cargar la lista al iniciar la app

        // Iniciar FireStore
        db = FirebaseFirestore.getInstance();

        // Enlazar las variables con el formulario creado
        txtId = findViewById(R.id.editTextID);
        txtNombre = findViewById(R.id.editTextNombre);
        txtDescripcion = findViewById(R.id.editTextDescripcion);
        spCategoria = findViewById(R.id.spinnerCategoria);
        spDuracion = findViewById(R.id.spinnerDuracion);
        spDificultad = findViewById(R.id.spinnerDificultad);

        lista = findViewById(R.id.lista);

    }

    // Metodo para enviar datos
    public void enviarDatosFirestore(View view){
        // Obtener los campos ingresando en el formulario
        String id = txtId.getText().toString();
        String nombre = txtNombre.getText().toString();
        String descripcion = txtDescripcion.getText().toString();
        String categoria = spCategoria.getSelectedItem().toString();
        String duracion = spDuracion.getSelectedItem().toString();
        String dificultad = spDificultad.getSelectedItem().toString();

        // Creamos un mapa con los datos para enviar
        Map<String, Object> curso = new HashMap<>();
        curso.put("id", id);
        curso.put("nombre", nombre);
        curso.put("descripcion", descripcion);
        curso.put("categoria", categoria);
        curso.put("duracion", duracion);
        curso.put("dificultad", dificultad);


        // Enviar los datos a FireBase
        db.collection("cursos") .document(id).set(curso).addOnSuccessListener(aVoid -> {
            Toast.makeText(FireBaseActivity.this, "Datos enviados a FireBase correctamente", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(FireBaseActivity.this, "Error al enviar datos a FireBase "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public void CargarLista(View view){
        cargarListaFireStore();
    }


    public void cargarListaFireStore(){
        // Obtenemos la instancia de FireStore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Hacemos una consulta a la coleccion llamada mascotas
        db.collection("cursos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    // Si la consulta es exitosa, procesara los documentos obtenidos y creara la lista para almacenar las cadenas de informacion de mascotas
                    List<String> listaCursos= new ArrayList<>();

                    // Recorrer todos los datos obtenidos de ordenandolos en la lista
                    for(QueryDocumentSnapshot document : task.getResult()){
                        String linea = "||" + document.getString("id") + "||" + document.getString("nombre")
                                +"||" + document.getString("descripcion") + "||" + document.getString("categoria")
                                +"||" + document.getString("duracion") + "||" + document.getString("dificultad");
                        listaCursos.add(linea);
                    }

                    // Crear un ArrayList con la lista de mascotas y establecer el adaptador en el ListView
                    ArrayAdapter<String> adaptador = new ArrayAdapter<>(FireBaseActivity.this, android.R.layout.simple_list_item_1, listaCursos);
                    lista.setAdapter(adaptador);
                } else {
                    // Se imprimira en consola si ahi errores al traer los datos
                    Log.e("TAG", "Error al obtener datos de FireStore", task.getException());
                }
            }
        });
    }

    public void onClickMqtt(View view) {
        Intent intent = new Intent(this, MqttActivity.class);
        startActivity(intent);
        finish();
    }
}
