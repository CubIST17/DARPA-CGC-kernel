echo "start running 'parse'..."

if [ $1 = "-original" ]; then

	./parse original/$2 -lparseAPI
	mv cfg.dot original/
    mv all_functions original/

	exit
fi

if [ $1 = "-static" ]; then
	./parse static/$2 -lparseAPI 
	mv cfg.dot static/
    mv all_functions static/

	exit
fi

if [ $1 = "-all" ]; then
	./parse original/$2 -lparseAPI
	mv cfg.dot original/
    mv all_functions original/


	./parse static/$2 -lparseAPI
	mv cfg.dot static/cfg.dot
    mv all_functions static/

	exit
fi
