package client.controllers;


import client.ClientService;
import client.dto.BookDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import shared.Action;
import shared.Request;
import shared.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KanbanPageController implements Refreshable
{

    @FXML
    private ListView<Node> notRead, reading, read;

    //koristi handleDeleteSelected()
    @FXML
    private Button deleteSelectedButton;

    //mapa koja pokazuje iz koje liste je kartica dosla
    private final Map<Node, ListView<Node>> cardToListMap = new HashMap<>();
    //mapa koja pokazuje za koju konkretno knijgu je kartica povezana
    private final Map<Node, BookDTO> cardToBookMap = new HashMap<>();

    //1 - notRead....
    private Map<Integer, ListView<Node>> statusToList;
    //notRead - 1...
    private Map<ListView<Node>, Integer> listToStatus;

    public void initialize()
    {
        // Initialize mappings
        statusToList = Map.of(
                1, notRead,
                2, reading,
                3, read
        );

        listToStatus = new HashMap<>();
        statusToList.forEach((status, list) -> listToStatus.put(list, status));

        //dodaj drag&drop handlere
        statusToList.values().forEach(this::setupDropTarget);

        loadBooksIntoBoard();
    }

    private void loadBooksIntoBoard() {
        cardToListMap.clear();
        cardToBookMap.clear();
        statusToList.values().forEach(list -> {
            if (list != null) list.getItems().clear();
        });

        try {
            Response response = ClientService.getInstance().sendRequest(new Request(Action.GET_BOOKS, null));
            if (response.isSuccess()) {
                List<BookDTO> books = (List<BookDTO>) response.getData();
                for (BookDTO book : books) {
                    Node card = createBookCard(book);

                    ListView<Node> targetList = statusToList.get(
                            book.getStatus() != null ? book.getStatus().getStatusId() : -1
                    );

                    if (targetList == null) {
                        System.out.println("Warning: Unknown statusId " +
                                (book.getStatus() != null ? book.getStatus().getStatusId() : "null") +
                                " for book " + book.getTitle()
                        );
                        continue; // skip adding this card
                    }

                    targetList.getItems().add(card);
                    cardToListMap.put(card, targetList);
                    cardToBookMap.put(card, book);
                }
            } else {
                System.out.println("Greska kod ucitavanja knjiga");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: Greska kod ucitavanja knjiga");
        }
    }


    private void setupDropTarget(ListView<Node> listView)
    {
        //event handleri koji mogu da se zakace za bilo koji node u javafx
        //drop target - tamo gde se draggable element ubacuje/izbacuje

        //kad je nesto prevuceno
        //event koji je konstantno trigerovan kada korisnik prevlaci preko ovog node-a
        listView.setOnDragOver(e ->
        {
            //proverava da li se kartica prevlaci preko iste liste, i ne dozvoljava drop
            //ukoliko je ista
            if (e.getGestureSource() != listView && e.getDragboard().hasString())
            {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        //kad je nesto ispusteno
        listView.setOnDragDropped(e ->
        {
            //privremeni nosilac podataka
            Dragboard db = e.getDragboard();

            //dummy
            if (db.hasString())
            {
                //pronadji originalnu listu iz koje je dosla,
                //kao i koja je to knjiga koju kartica opisuje
                Node dragged = (Node) e.getGestureSource();
                ListView<Node> originList = cardToListMap.get(dragged);
                BookDTO book = cardToBookMap.get(dragged);

                //skida karticu iz originalne liste i  dodaje novu, vizuelno
                originList.getItems().remove(dragged);
                listView.getItems().add(dragged);
                cardToListMap.put(dragged, listView);

                int newStatusId = listToStatus.get(listView);
                if (book.getStatus().getStatusId() != newStatusId)
                {
                    book.getStatus().setStatusId(newStatusId);
                    try
                    {
                        Response updateResponse = ClientService.getInstance().sendRequest(
                                new Request(Action.UPDATE_BOOK, book)
                        );
                        if (!updateResponse.isSuccess())
                        {
                            System.out.println("Nije uspelo azuriranje astatusa knjige");
                        }
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                        System.out.println("Error: Neuspesno azuriranje knjige");
                    }
                }

                //uspesno
                e.setDropCompleted(true);

            } else
            {
                e.setDropCompleted(false);
            }
            e.consume();
        });
    }

    @FXML
    private void handleDeleteSelected()
    {
        //prolazi kroz svaku listu, poenta je da se proveri da li je bilo koja selektovana
        //tj neki node u njoj
        for (ListView<Node> listView : statusToList.values())
        {
            Node selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null)
            {
                BookDTO book = cardToBookMap.get(selected);

                try
                {
                    Response response = ClientService.getInstance().sendRequest(
                            new Request(Action.DELETE_BOOK, book.getId())
                    );
                    if (response.isSuccess())
                    {
                        listView.getItems().remove(selected);
                        cardToBookMap.remove(selected);
                        cardToListMap.remove(selected);
                        listView.getSelectionModel().clearSelection();
                    } else
                    {
                        System.out.println("Neuspesno brisanje knjige.");
                    }
                    return;

                } catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("Error: Neuspesno brisanje knjige.");
                    return;
                }
            }
        }
    }


    private Node createBookCard(BookDTO bookDto)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/bookCard.fxml"));
            HBox cardRoot = loader.load();
            BookCardController controller = loader.getController();
            controller.setBook(bookDto);
            setupDragEvents(cardRoot);
            return cardRoot;

        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private void setupDragEvents(HBox card)
    {
        //korisnik je kliknuo element i dovolhno pomerio mis da se racuna
        //kao da je prevukao element
        card.setOnDragDetected(e ->
        {
            //dragboard je poput temporary clipboard-a
            //transfermode moze biti i copy

            //obavestava javafx da ovaj element zapocinje draganddrop sto kreira dragboard
            Dragboard db = card.startDragAndDrop(TransferMode.MOVE);

            //ovaj objekat drzi podatke koje zelimo da se prevlace
            ClipboardContent content = new ClipboardContent();
            content.putString("");  //MORA DA POSTOJI STRING KAKO BI DRAG AND DROP RADIO
            db.setContent(content);
            e.consume();
        });
        card.setOnDragDone(e -> e.consume());
    }

    @Override
    public void refresh()
    {
        loadBooksIntoBoard();
    }
}
