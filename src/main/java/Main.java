import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        //http://localhost:4567/todos
        List<TodoItem>todos = new ArrayList<>(
                Arrays.asList(
                        TodoItem.create(1L, "Kochen"),TodoItem.create(2L, "Radeln"),
                        TodoItem.create(3L, "Einkaufen"), TodoItem.create(4L, "Reise buchen"),
                        TodoItem.create(5L, "Wäsche waschen"))
                );
        System.out.println(todos.size());

        //curl http://localhost:4567/todos
        get("/todos","application/json", (req, res) -> {
                //content type verändern der Response als info für den client welches format
                res.header("content-type", "application/json;charset=utf-8");

                return new JSONSerializer().serialize(todos);
        });

        //curl http://localhost:4567/todos/1
        get("/todos/:id","application/json", (req, res) -> {
            //content type verändern der Response als info für den client welches format
            res.header("content-type", "application/json;charset=utf-8");
            final Long idToRead = Long.valueOf(req.params("id"));

            for (var item : todos){
                if(item.id.equals(idToRead)){
                    res.status(200);
                    return new JSONSerializer().serialize(item);
                }
            }
            //null = 404 statuscode
            return null;
        });

        // curl -i -X DELETE -H 'content-type: application/json' http://localhost:4567/todos/:id
        //erstellt einen DELETE Aufruf auf ein Item mit der id xy
        delete("/todos/:id","application/json", (req, res) -> {
            //content type verändern der Response als info für den client welches format
            res.header("content-type", "application/json;charset=utf-8");
            //Liest die Id aus der URL
            Long idToDelete = Long.valueOf(req.params().get(":id"));
            int oldItemsSize = todos.size();

            // Durchsucht die ArrayList: id die aus Get gelesen wird mit der gleichen ID vom Long des Todoitems
            //Wenn gleiche ID dann gibt die compare 0 zurück.--> folge item wird aus arrayList entfernt.
            todos.removeIf(todoItem -> todoItem.id.equals(idToDelete));

            return new JSONSerializer().serialize(todos);

        });
        //curl -X POST http://localhost:4567/todos -H 'content-type: application/json' -d '{"description":"auto reparieren"}'
        post("/todos", "application/json", (req,res) -> {
            res.header("content-type", "application/json;charset=utf-8");


            // request body deserialisieren (json -> Objekt)
            TodoItem newItem = new JSONSerializer().deserialize(req.body(), new TypeReference<TodoItem>() {});
            System.out.println(newItem);
            System.out.println(req.body());


            todos.add(newItem);
            res.status(202);
            //gibt newItem als String zurück
            return new JSONSerializer().serialize(newItem);


        });


    }
}

