if [ -z $1 ]; then 
	echo "please specify parameter"
	exit
fi

if [ $1 = "-original" ]; then
	echo "plotting...original..."
	dot -Tps original/cfg.dot -o original/cfg.eps
fi

if [ $1 = "-static" ]; then
	echo "plotting...static..."
	dot -Tps static/cfg.dot -o static/cfg.eps
fi


