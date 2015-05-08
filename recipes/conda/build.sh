cp -r $RECIPE_DIR/../../* .

IRIS=$PREFIX/opt/iris
mkdir -p $IRIS

mvn -DskipTests=true -Dsherpa=no package
cd iris/target
chmod u+x Iris
cp Iris $IRIS/launch
cp -r lib $IRIS
cp *.jar $IRIS
cp -r LICENSES $IRIS
cp README.txt $IRIS
cp -r examples $IRIS

echo "$PREFIX/opt/iris/launch \$@" > iris
chmod u+x iris
mkdir -p $PREFIX/bin/
cp iris $PREFIX/bin/
