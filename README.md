# Main Design

There are three main components which make up the system, the RSS parser, the recommendation
algorithm and the UI.

The RSS parser serves to retrieve and parse RSS documents, parsed feeds are then stored and can
be used by the recommendation system and the UI.

The recommendation system takes feed objects created by the RSS parser and generates
recommendations based on various factor. Since the recommendation system runs offline, the only
data available to it is the RSS data, therefore it will fall into the content-based recommender
category.

Finally, the UI takes both the parsed feeds and the recommendations and presents them to the user
in a readable and coherent manner.


The recommendation system consists of many small parts; together these parts form a pipeline
where the resulting calculations from preceding parts are required by subsequent parts in order to
function correctly.

The two main parts of this recommendation system are TF-IDF and SVD. The former allows the
system to determine the most important terms in a document and provided a general idea of the
topic. TF-IDF alone can provide a set of recommendations, however they will suffer from low
coverage and low diversity. The latter allows the system to find latent factor within a matrix by
decomposing the matrix into three smaller matrices. In a recommendation system this will uncover
latent factor and relations between items which aren’t apparent in the original matrix.

During the first stage of the pipeline, all feed items are fed through the TF-IDF calculator along with
terms or topics which the user has shown interest in. This generates a score for each feed item, the
score determines if a topic appears in a feed item and how important that topic is to each feed item.
Thus, higher ranking feed items will not only contain references to these topics, but the topics will
also be integral parts of the feed items themselves.

Once this stage of the pipeline is complete, we should have a list of feed items concerning topics the
user is interested in. However, this is not enough for a robust recommendation system, it only
provides the user with feed items which the user has explicitly asked for. The system should also be
able to determine feed items which may have hidden relations to feed items which the user already
enjoys. This means finding similarities between items which are not explicitly defined, this is where
the next stage of the pipeline comes in.

The second and more complex part of the pipeline aims to uncover latent relations between feed
items which the user already enjoys and feed items the user has not tried.

The most accurate recommendations will be those which are most similar to items the user already
enjoys. Therefore, we need to find items which are “closest” to those provided by the last step in the
pipeline. An interesting approach would be to assign each feed item with a position in n dimensional
space and then use the cosine similarity distance formula to determine how close items are.
However, we need to determine a way to mathematically represent the features of an item in a
quantifiable manner and translate those into points in n dimensional space, before we can calculate
how close they are.


Whilst the user interface does not affect the quality of recommendations or the effectiveness of the
RSS parser, it is still a “critical part of any software product” and can have a profound effect on
the user’s experience. Whilst keeping this in mind various design guidelines were followed to
produce a user interface design process which adheres to three main principles.

1. Maintaining consistency
2. Focusing on content
3. Minimising required interaction


For long term storage there are two sets of data the program needs to save for it to function, the RSS data and
the program configurations.

The program configuration consists of basic key-values pairs and can be store as a JSON file once
serialised. JSON “is a text format that is completely language independent” [23] derived
from JavaScript. A settings object within the program can be serialised and stored as a JSON file and
de-serialised when read.

However, the RSS data has meaningful structure which needs to be preserved and is therefore
stored in a relational database.

Note: For the Windows operating system all data is stored in “User/AppData/Local/RSSReader/”.

![](/DemoVideo.mp4)
