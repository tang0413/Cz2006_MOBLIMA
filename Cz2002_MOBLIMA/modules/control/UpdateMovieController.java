package modules.control;

import modules.boundary.Console;
import modules.data.DataBase;
import modules.entity.movie.Actor;
import modules.entity.movie.Director;
import modules.entity.movie.Movie;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class UpdateMovieController extends BaseController { //TODO to be reused by passing index from ListMovieInfoController, needs to be updated
    public UpdateMovieController(Console inheritedConsole) {
        super(inheritedConsole);
    }

    @Override
    public void enter() {

    }

    public void enter(int actionChoice, int movieId) {
        while (true){
            try{
                if (actionChoice == 0){
                    insetNewMovie();
                    return;
                }
                Movie chosenMovie = DataBase.getMovieById(movieId);
                alterMovie(chosenMovie, actionChoice);
                return;
            } catch (Exception e){
                this.console.log(e.getMessage());
                return;
            }
        }
    }

    private void insetNewMovie() {
         this.console.logText("Please key in the following necessary information");
         ArrayList<String> newMovieParam = new ArrayList();
         int newMovieId = DataBase.getNewId(Movie.class);
         newMovieParam.add(Integer.toString(newMovieId));
         newMovieParam.add(this.console.getStr("Movie Name"));
         newMovieParam.add(this.console.getStr("Description"));
         newMovieParam.add(this.console.getMovieType("Status"));
         newMovieParam.add(this.console.getStr("Movie Type"));
         newMovieParam.add(this.console.getStr("Category"));//TODO add checking
         this.console.logReminder("Please separate names by ',' with no space");
         String DirectorList = this.console.getStr("Director(s)");
         String Cast = this.console.getStr("Cast(s)");
         try{
             alterDirector(DirectorList, newMovieId);
             alterCast(Cast, newMovieId);
             Movie newMovie = new Movie(newMovieParam);
             DataBase.setData(MOVIEFILENAME, newMovie);
             autoReturn();
         } catch (Exception e){
             this.console.log(e.getMessage());
         }
    }

    private void alterMovie(Movie movieToChange, int actionChoice) throws IOException, InterruptedException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        String newValue;
        switch(actionChoice){ //TODO get rid of this stupid method!!!!
            case 1:
                newValue = this.console.getStr("Please enter a new Name");
                movieToChange.setName(newValue);
                break;
            case 2:
                newValue = this.console.getStr("Please enter a new type");
                movieToChange.setType(newValue);
                break;
            case 3:
                newValue = this.console.getStr("Please enter a new category (Movie Rating)");
                movieToChange.setCat(newValue);
                break;
            case 4:
                newValue = this.console.getStr("Please enter a new description");
                movieToChange.setDescription(newValue);
                break;
            case 5:
                this.console.logReminder("Please separate names by ',' with no space");
                newValue = this.console.getStr("Please enter new director(s) to be added");//TODO so far can only add
                alterDirector(newValue, movieToChange.getId());
                break;
            case 6:
                this.console.logReminder("Please separate names by ',' with no space");
                newValue = this.console.getStr("Please enter new cast to be added");
                alterCast(newValue, movieToChange.getId());
                break;
            case 7:
                newValue = this.console.getMovieType("Please enter a new status");
                movieToChange.setStatus(newValue);
                break;
        }
        DataBase.setData(MOVIEFILENAME, movieToChange);
        autoReturn();
    }

    private void alterDirector(String DirectorList, int newMovieId) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String splittedInMovie[] = DirectorList.split(",");
        ArrayList<String> directorNames= new ArrayList<>( Arrays.asList(splittedInMovie));
        ArrayList<Director> currentDirectorList = DataBase.readList(DIRECTORFILENAME, Director.class);
        for (Director d :currentDirectorList){
            if(directorNames.contains(d.getName())){
                d.addInMovie(newMovieId);
                directorNames.remove(d.getName());
            }
        }
        if(directorNames.size() != 0){
            for (String dName: directorNames){
                ArrayList<String> newDirectorParam = new ArrayList<>();
                newDirectorParam.add(Integer.toString(DataBase.getNewId(Director.class)));
                newDirectorParam.add(dName);
                newDirectorParam.add(Integer.toString(newMovieId));
                Director newDirector = new Director(newDirectorParam);
                DataBase.setData(DIRECTORFILENAME, newDirector);
            }
        }
    }

    private void alterCast(String Cast, int newMovieId) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String splittedInMovie[] = Cast.split(","); //TODO get rid off this similar code
        ArrayList<String> castNames= new ArrayList<>( Arrays.asList(splittedInMovie));
        ArrayList<Actor> currentCastList = DataBase.readList(CASTFILENAME, Actor.class);
        for (Actor a :currentCastList){
            if(castNames.contains(a.getName())){
                a.addInMovie(newMovieId);
                castNames.remove(a.getName());
            }
        }
        if(castNames.size() != 0){
            for (String aName: castNames){
                ArrayList<String> newActorParam = new ArrayList<>();
                newActorParam.add(Integer.toString(DataBase.getNewId(Actor.class)));
                newActorParam.add(aName);
                newActorParam.add(Integer.toString(newMovieId));
                Actor newActor = new Actor(newActorParam);
                DataBase.setData(CASTFILENAME, newActor);
            }
        }
    }

    private void autoReturn() throws InterruptedException {
        this.console.logReminder("Updated successfully! Returning to the previous page...");
        TimeUnit.SECONDS.sleep((long)2.5);
        return;
    }
}
