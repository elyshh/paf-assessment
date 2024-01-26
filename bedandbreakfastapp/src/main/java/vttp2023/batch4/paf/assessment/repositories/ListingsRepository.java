package vttp2023.batch4.paf.assessment.repositories;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.assessment.Utils;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;

@Repository
public class ListingsRepository {
	
	// You may add additional dependency injections

	@Autowired
	private MongoTemplate template;

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	 *
	 * db.listings.aggregate([
	 *	{
	 *		$match: {
	 *			"address.country": {
	 *				$regex: "Australia",
	 *				$options: "i"
	 *			}
	 *		}
	 *	},
	 *	{
	 *		$match: {
	 *			"address.suburb": {
	 *				$nin: [""]
	 *			}
	 *		}
	 *	},
	 *	{
	 *		$group: {
	 *			_id: "$address.suburb"
	 *		}
	 *	}
	 * ]);
	 */
	public List<String> getSuburbs(String country) {
        MatchOperation matchCountry = Aggregation.match(Criteria.where("address.country").regex(country, "i"));
        MatchOperation matchSuburbNotNullOrEmpty = Aggregation.match(Criteria.where("address.suburb").nin(""));
        
		GroupOperation groupBySuburb = Aggregation.group("address.suburb");
        ProjectionOperation projectToId = Aggregation.project("_id");
        Aggregation aggregation = Aggregation.newAggregation(matchCountry, matchSuburbNotNullOrEmpty, groupBySuburb, projectToId);

        List<String> results = template.aggregate(aggregation, "listings", String.class)
                .getMappedResults()
                .stream()
                .map(result -> result.substring(result.indexOf(":")+3, result.length()-2)) // Extract substring after ": " and remove the trailing "}"
                .collect(Collectors.toList());

        return results;
    }

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	 *
	 *	db.listings.aggregate([
	 *		{
	 *			$match: {
	 *				"address.suburb": { $regex: "Lilyfield/Rozelle", $options: "i" },
	 *				"price": { $lte: NumberDecimal("250.00") },
	 *				"accommodates": { $lte: 2 },
	 *				"min_nights": { $gte: 2 }
	 *			}
	 *		},    
	 *		{
	 *			$project: {
	 *				"_id": 1,
	 *				"name": 1,
	 *				"accommodates": 1,
	 *				"price": 1
	 *			}
	 *		},
	 *		{
	 *			$sort: { "price": -1 }
	 *		}
	 *	])
	 */
	public List<AccommodationSummary> findListings(String suburb, int persons, int duration, float priceRange) {
        MatchOperation matchOps = Aggregation.match(
                Criteria.where("address.suburb").regex(suburb, "i")
                        .and("price").lte(priceRange)
                        .and("accommodates").lte(persons)
                        .and("min_nights").gte(duration)
        );

        ProjectionOperation projectOps = Aggregation.project("_id", "name", "accommodates", "price");
        SortOperation sort = Aggregation.sort(Sort.by(Sort.Direction.DESC, "price"));
        Aggregation pipeline = Aggregation.newAggregation(matchOps, projectOps, sort);

        List<AccommodationSummary> accSummaryList = new LinkedList<>();

        template.aggregate(pipeline, "listings", Document.class).forEach(doc -> {
            String id = doc.getString("_id");
            String name = doc.getString("name");
            Integer accommodates = doc.getInteger("accommodates");
            Float price = doc.get("price", Number.class).floatValue();

            AccommodationSummary accSummary = new AccommodationSummary();
			accSummary.setId(id);
			accSummary.setName(name);
			accSummary.setAccomodates(accommodates);
			accSummary.setPrice(price);
            accSummaryList.add(accSummary);
        });
        return accSummaryList;
		
    }

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public Optional<Accommodation> findAccommodatationById(String id) {
		Criteria criteria = Criteria.where("_id").is(id);
		Query query = Query.query(criteria);

		List<Document> result = template.find(query, Document.class, "listings");
		if (result.size() <= 0)
			return Optional.empty();

		return Optional.of(Utils.toAccommodation(result.getFirst()));
	}

}


