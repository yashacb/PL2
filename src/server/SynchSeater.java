package server;

import server.gui.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SynchSeater {
    /**
     * "seats" ArrayList represents the seating arrangement.
     */
    private int size;
    private List<Student> seats = new ArrayList<>();

    /**
     * @param size - The size of the seats array.
     */
    SynchSeater(int size) {
        this.size = size ;
        for (int i = 0; i < size; i++) {
            seats.add(null) ;
        }
    }

    /**
     * @param rollNo - Roll no. of the student about to claim the seat.
     * @param name - Name of the student about to claim the seat.
     * @param seat - Seat the student is trying to claim.
     * @return - Returns if the user can claim the seat.
     * This method has to be synchronized to avoid race conditions.
     */
    public synchronized boolean claimSeat(String rollNo, String name, int seat) {
        if (seats.get(seat) != null)
            return false;
        seats.set(seat, new Student(rollNo, name));
        return true;
    }

    /**
     * @param seat - The query seat.
     * @return - Returns true if the 'seat' is empty.
     * This method has to be synchronized to avoid race conditions.
     */
    public synchronized boolean isEmpty(int seat) {
        return seats.get(seat) == null;
    }

    /**
     * @return - The size of the seats array.
     */
    public int getSize() {
        return size;
    }

    /**
     * @param i - The index of the required student.
     * @return - The student object corresponding to index i.
     */
    public Student getStudent(int i){
        if(i < seats.size())
            return seats.get(i) ;
        return null ;
    }

    /**
     * Fills the seats with default values.
     */
    public void fillDefaults(){
        for(int i = 1 ; i <= seats.size() ; i++){
            seats.set(i - 1, new Student(String.valueOf(1000 + i), "Yash"));
        }
    }

    public long occSeats(){
        return seats.stream().filter(Objects::nonNull).count();
    }
}
