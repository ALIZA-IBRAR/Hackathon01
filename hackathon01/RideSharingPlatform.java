import java.util.*;

class User {
    String name;
    String phone;
    String location;

    public User(String name, String phone, String location) {
        this.name = name;
        this.phone = phone;
        this.location = location;
    }
}

class Driver extends User {
    Vehicle vehicle;
    boolean isAvailable;
    float rating;

    public Driver(String name, String phone, String location, Vehicle vehicle, float rating) {
        super(name, phone, location);
        this.vehicle = vehicle;
        this.isAvailable = true;
        this.rating = rating;
    }
}

abstract class Vehicle {
    int capacity;
    boolean isOutOfService;

    public Vehicle(int capacity, boolean isOutOfService) {
        this.capacity = capacity;
        this.isOutOfService = isOutOfService;
    }

    abstract String getType();

    abstract double calculateFare(double distance);
}

class Bike extends Vehicle {
    public Bike(boolean isOutOfService) { super(1, isOutOfService); }
    @Override String getType() { return "Bike"; }
    @Override double calculateFare(double distance) { return distance * 0.5; }
}

class AutoRickshaw extends Vehicle {
    public AutoRickshaw(boolean isOutOfService) { super(3, isOutOfService); }
    @Override String getType() { return "AutoRickshaw"; }
    @Override double calculateFare(double distance) { return distance * 0.8; }
}

class Sedan extends Vehicle {
    public Sedan(boolean isOutOfService) { super(4, isOutOfService); }
    @Override String getType() { return "Sedan"; }
    @Override double calculateFare(double distance) { return distance * 1.0; }
}

class SUV extends Vehicle {
    public SUV(boolean isOutOfService) { super(6, isOutOfService); }
    @Override String getType() { return "SUV"; }
    @Override double calculateFare(double distance) { return distance * 1.5; }
}

class Ride {
    String rideId;
    User rider;
    Driver driver;
    String pickupLocation;
    String dropLocation;
    double distance;

    public Ride(String rideId, User rider, Driver driver, String pickupLocation, String dropLocation, double distance) {
        this.rideId = rideId;
        this.rider = rider;
        this.driver = driver;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.distance = distance;
    }
}

class RideBooking {
    public Ride bookRide(User user, Driver driver, String pickup, String drop, double distance) {
        if (driver.isAvailable && !driver.vehicle.isOutOfService) {
            driver.isAvailable = false;
            Ride ride = new Ride("RIDE" + System.currentTimeMillis(), user, driver, pickup, drop, distance);
            return ride;
        } else {
            System.out.println("Driver not available or vehicle out of service.");
            return null;
        }
    }
}

class Notification {
    public void send(Ride ride) {
        System.out.println("Notification sent to Driver " + ride.driver.name + " and Rider " + ride.rider.name);
    }

    public void accept(Ride ride) {
        System.out.println("Driver " + ride.driver.name + " accepted the ride.");
    }

    public void onTheWay(Ride ride) {
        System.out.println("Driver " + ride.driver.name + " is on the way to pick you up.");
    }

    public void rideComplete(Ride ride) {
        System.out.println("Ride complete from " + ride.pickupLocation + " to " + ride.dropLocation);
    }

    public void rideConfirmedAfterPayment(Ride ride) {
        System.out.println("Payment received. Your ride has been confirmed.");
        System.out.println("Please wait at your pickup location: " + ride.pickupLocation);
    }
}

interface PaymentStrategy {
    void pay(double amount);
}

class UPI implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " via UPI");
    }
}
class NetBanking implements PaymentStrategy{
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " via NetBanking");
    }
}
class Cash implements PaymentStrategy{
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " via Cash");
    }
}

class MatchingDriver {
    public List<Driver> findBest(List<Driver> drivers, String location) {
        List<Driver> matched = new ArrayList<>();
        for (Driver d : drivers) {
            if (d.isAvailable && d.location.equalsIgnoreCase(location)) {
                matched.add(d);
            }
        }
        matched.sort((a, b) -> Float.compare(b.rating, a.rating)); // highest rating first
        return matched;
    }
}

class Feedback {
    public void giveFeedback(int rating) {
        System.out.println("User feedback received: " + rating);
        System.out.println("Thank you for your feedback!");
        System.out.println("Have a safe and happy journey!");
    }
}

public class RideSharingPlatform {
    public static void main(String[] args) throws InterruptedException {
        Vehicle sedan = new Sedan(false);
        Vehicle suv = new SUV(false);
        Vehicle bike = new Bike(true);

        Driver d1 = new Driver("Amit", "98765", "Patna", sedan, 4.9f);
        Driver d2 = new Driver("Neha", "91234", "Patna", suv, 4.7f);
        Driver d3 = new Driver("Ravi", "99999", "Delhi", bike, 4.8f);

        User user = new User("Alok", "88888", "Patna");

        List<Driver> allDrivers = Arrays.asList(d1, d2, d3);
        MatchingDriver matcher = new MatchingDriver();
        List<Driver> matched = matcher.findBest(allDrivers, user.location);

        if (matched.isEmpty()) {
            System.out.println("No drivers available nearby.");
            return;
        }

        System.out.println("Top drivers near you:");
        for (Driver d : matched) {
            System.out.println("- " + d.name + " | Rating: " + d.rating + " | Vehicle: " + d.vehicle.getType());
        }

        double distance = 12.5;

        RideBooking booking = new RideBooking();
        Ride ride = booking.bookRide(user, matched.get(0), "Gandhi Maidan", "Airport", distance);

        if (ride != null) {
            Notification n = new Notification();

            n.send(ride);
            Thread.sleep(1000);

            n.accept(ride);
            Thread.sleep(1000);

            double fare = ride.driver.vehicle.calculateFare(ride.distance);
            System.out.printf("Total fare for %.2f km = $%.2f%n", ride.distance, fare);
            Thread.sleep(1000);

            PaymentStrategy payment = new UPI();
            payment.pay(fare);
            Thread.sleep(1000);

            n.rideConfirmedAfterPayment(ride);
            Thread.sleep(1000);

            n.onTheWay(ride);
            Thread.sleep(1000);

            n.rideComplete(ride);
            Thread.sleep(1000);

            Feedback feedback = new Feedback();

           
            feedback.giveFeedback(4);
        }
    }
}
