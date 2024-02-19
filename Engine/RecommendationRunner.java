import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class RecommendationRunner implements Recommender {
    HashSet<String> moviesForRating = new HashSet<String>();

    public ArrayList<String> getItemsToRate() {
        ArrayList<String> movies = new ArrayList<String>();
        MovieDatabase.initialize("ratedmoviesfull.csv");

        ArrayList<String> allFilms = MovieDatabase.filterBy(new TrueFilter());
        pickRandomItems(allFilms, 3);
        ArrayList<String> actionMovies = MovieDatabase.filterBy(new GenreFilter("Action"));
        pickRandomItems(actionMovies, 2);
        ArrayList<String> dramaMovies = MovieDatabase.filterBy(new GenreFilter("Drama"));
        pickRandomItems(dramaMovies, 2);
        ArrayList<String> comedyFilms = MovieDatabase.filterBy(new GenreFilter("Comedy"));
        pickRandomItems(comedyFilms, 2);
        ArrayList<String> thrillerFilms = MovieDatabase.filterBy(new GenreFilter("Thriller"));
        pickRandomItems(thrillerFilms, 1);
        ArrayList<String> adventureFilms = MovieDatabase.filterBy(new GenreFilter("Adventure"));
        pickRandomItems(adventureFilms, 2);
        ArrayList<String> recentFilms = MovieDatabase.filterBy(new YearAfterFilter(2014));
        pickRandomItems(recentFilms, 4);
        
        for(String id : moviesForRating) {
            movies.add(id);
        }

        return movies;
    }

    private void pickRandomItems(ArrayList<String> movieList, int nOfItemsToPick) {
        Random randomNgenerator = new Random();
        for(int i = 0; i < nOfItemsToPick; i++) {
            int randomIndex = randomNgenerator.nextInt(movieList.size());

            String id = movieList.get(randomIndex);
            moviesForRating.add(id);
        }
    }

    public void printRecommendationsFor(String webRaterID) {
        FourthRatings recommendator = new FourthRatings();
        ArrayList<Rating> recommendations = recommendator.getSimilarRatings(webRaterID, 40, 8);

        if(recommendations.isEmpty()) {
            recommendations = recommendator.getSimilarRatings(webRaterID, 40, 1);
        }

        if(recommendations.isEmpty()) {
            System.out.println("No films in the list.");
            return;
        }

        System.out.println("<table>");
        System.out.println("<title>Movie Recommendations</title>");
        System.out.println("<style>\r\n" + //
                "        body {\r\n" + //
                "            font-family: Arial, sans-serif;\r\n" + //
                "            background-color: #f0f0f0;\r\n" + //
                "            margin: 0;\r\n" + //
                "            padding: 0;\r\n" + //
                "        }\r\n" + //
                "\r\n" + //
                "        .movie-container {\r\n" + //
                "            display: inline-block;\r\n" + //
                "            flex-wrap: wrap; /* Allow wrapping to the next line */\r\n" + //
                "            justify-content: flex-start; /* Align items to the start of the container */\r\n" + //
                "            padding: 20px;\r\n" + //
                "        }\r\n" + //
                "\r\n" + //
                "        .movie-card {\r\n" + //
                "            margin-right: 20px;\r\n" + //
                "            margin-bottom: 20px; /* Add margin to create space between movie cards */\r\n" + //
                "            width: 250px;\r\n" + //
                "            border: 1px solid #ccc;\r\n" + //
                "            border-radius: 10px;\r\n" + //
                "            overflow: hidden;\r\n" + //
                "            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);\r\n" + //
                "        }\r\n" + //
                "\r\n" + //
                "        .movie-card img {\r\n" + //
                "            width: 100%;\r\n" + //
                "            height: auto;\r\n" + //
                "            border-radius: 10px 10px 0 0;\r\n" + //
                "        }\r\n" + //
                "\r\n" + //
                "        .movie-info {\r\n" + //
                "            padding: 10px;\r\n" + //
                "        }\r\n" + //
                "\r\n" + //
                "        .movie-title {\r\n" + //
                "            font-weight: bold;\r\n" + //
                "            font-size: 16px;\r\n" + //
                "            margin-bottom: 5px;\r\n" + //
                "        }\r\n" + //
                "\r\n" + //
                "        .movie-description {\r\n" + //
                "            font-size: 14px;\r\n" + //
                "        }\r\n" + //
                "    </style><body>");        
        int nOfMovies = 0;
        for(Rating id : recommendations) {
            System.out.println("<div class=\"movie-container\"><div class=\"movie-card\">\r\n" + //
                    "        <img src=" + MovieDatabase.getPoster(id.getItem()) + " alt=\"Movie 1\">\r\n" + //
                    "        <div class=\"movie-info\">\r\n" + //
                    "            <div class=\"movie-title\">"+MovieDatabase.getTitle(id.getItem())+"</div>\r\n" + //
                    "            <div class=\"movie-description\">" + MovieDatabase.getDirector(id.getItem()) + "</div>\r\n" + //
                    "        </div>\r\n" + //
                    "    </div></div>");
            nOfMovies++;
            if(nOfMovies == 20) break;
        }
        System.out.println("</body>");
        System.out.println("</table>");

    }

}