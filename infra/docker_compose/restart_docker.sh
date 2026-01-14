
echo
docker compose down

echo

json_file="../config/browsers.json"

if ! command -v jq &> /dev/null; then
    echo "❌ jq is not installed. Please install jq and try again."
    exit 1
fi

images=$(jq -r '.. | objects | select(.image) | .image' "$json_file")

for image in $images; do
    echo "Pulling $image..."
    docker pull "$image"
done

echo
docker compose up -d

echo "Waiting for TeamCity..."
for i in {1..60}; do
  if curl -s http://localhost:8111 > /dev/null; then
    echo "TeamCity is ready!"
    break
  fi
  echo "TeamCity not ready yet, waiting 5 seconds..."
  sleep 5
done
