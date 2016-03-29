docker run --rm --volumes-from mongo_shopping_data -v /tmp:/backup debian tar cvf /backup/backup.tar /data/db
