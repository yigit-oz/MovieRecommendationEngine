import java.util.ArrayList;
import java.util.Collections;

public class FourthRatings {
    
    private double getAverageByID(String id, int minimalRaters) {
        double ratingSum = 0;
        int totalRaters = 0;

        for(Rater r : RaterDatabase.getRaters()) {
            double currRating = r.getRating(id);
            if(currRating != -1) {
                ratingSum += currRating;
                totalRaters++;
            }
        }

        if(totalRaters < minimalRaters) {
            return 0;
        }

        return ratingSum / totalRaters;
        
    }

    public ArrayList<Rating> getAverageRatings(int minimalRaters) {
        ArrayList<Rating> ratings = new ArrayList<Rating>();
        ArrayList<String> movies = MovieDatabase.filterBy(new TrueFilter());
        
        for(String m : movies) {
            double currRating = getAverageByID(m, minimalRaters);
            if(currRating != 0) {
                Rating rating = new Rating(m, currRating);
                ratings.add(rating);
            }
        }

        return ratings;

    }

    public ArrayList<Rating> getAverageRatingsByFilter(int minimalRaters, Filter filterCriteria) {
        ArrayList<Rating> ratings = new ArrayList<Rating>();
        ArrayList<String> movies = MovieDatabase.filterBy(new TrueFilter());

        for(String m : movies) {
            double currRating = getAverageByID(m, minimalRaters);
            if(currRating != 0 && filterCriteria.satisfies(m)) {
                Rating rating = new Rating(m, currRating);
                ratings.add(rating);
            }
        }

        return ratings;

    }

    private double dotProduct(Rater me, Rater r) {
        int smallerRatings = me.numRatings(), largerRatings = r.numRatings();
        double result = 0;

        if(smallerRatings > largerRatings) {
            Rater temp = me;
            me = r;
            r = temp;
        }

        for(String movie : me.getItemsRated()) {
            if(r.hasRating(movie)) {
                double firstRating = me.getRating(movie) - 5, 
                       secondRating = r.getRating(movie) - 5;
                result += firstRating * secondRating;       
            }
        }

        return result;
    }

    private ArrayList<Rating> getSimilarities(String id) {
        ArrayList<Rating> result = new ArrayList<Rating>();
        Rater me = RaterDatabase.getRater(id);

        for(Rater r : RaterDatabase.getRaters()) {
            if(!r.equals(me)) {
                    double dotProduct = dotProduct(me, r);
                if(dotProduct > 0) {
                    Rating similarityRating = new Rating(r.getID(), dotProduct);
                    result.add(similarityRating);
                }
            }
        }

        Collections.sort(result, Collections.reverseOrder());
        return result;
    }

    public ArrayList<Rating> getSimilarRatings(String id, int numSimilarRaters, int minimalRaters) {
        ArrayList<Rating> similarityRatings = getSimilarities(id);
        ArrayList<Rating> movieRatings = new ArrayList<Rating>();
        ArrayList<String> movies = MovieDatabase.filterBy(new TrueFilter());
        if(similarityRatings.isEmpty()) {
            return movieRatings;
        }
        if(similarityRatings.size() <= numSimilarRaters) {
            numSimilarRaters = similarityRatings.size();
        }

        for(String movie : movies) {
            double ratingSum = 0, weightedAverage = 0;
            int totalRaters = 0;

            for(int i = 0; i<numSimilarRaters; i++) {
                Rating currRating = similarityRatings.get(i);
                Rater currRater = RaterDatabase.getRater(currRating.getItem());
                if(currRater.hasRating(movie)) {
                    ratingSum += currRater.getRating(movie) * currRating.getValue();
                    totalRaters++;
                }
            }

            if(totalRaters >= minimalRaters && totalRaters != 0) {
                weightedAverage = ratingSum / totalRaters;
                movieRatings.add(new Rating(movie, weightedAverage));
            }
        }

        Collections.sort(movieRatings, Collections.reverseOrder());
        return movieRatings;
    }

    public ArrayList<Rating> getSimilarRatingsByFilter(String id, int numSimilarRaters, int minimalRaters, Filter filterCriteria) {
        ArrayList<Rating> similarityRatings = getSimilarities(id);
        ArrayList<Rating> movieRatings = new ArrayList<Rating>();
        ArrayList<String> movies = MovieDatabase.filterBy(new TrueFilter());
        if(similarityRatings.isEmpty()) {
            return movieRatings;
        }
        if(similarityRatings.size() <= numSimilarRaters) {
            numSimilarRaters = similarityRatings.size();
        }

        for(String movie : movies) {
            if(filterCriteria.satisfies(movie)) {
                double ratingSum = 0, weightedAverage = 0;
                int totalRaters = 0;

                for(int i = 0; i<numSimilarRaters; i++) {
                    Rating currRating = similarityRatings.get(i);
                    Rater currRater = RaterDatabase.getRater(currRating.getItem());
                    if(currRater.hasRating(movie)) {
                        ratingSum += currRater.getRating(movie) * currRating.getValue();
                        totalRaters++;
                    }
                }

                if(totalRaters >= minimalRaters && totalRaters != 0) {
                    weightedAverage = ratingSum / totalRaters;
                    movieRatings.add(new Rating(movie, weightedAverage));
                }
            }
        }

        Collections.sort(movieRatings, Collections.reverseOrder());
        return movieRatings;
    }

}
