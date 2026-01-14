
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
echo "=== Fixing permissions for TeamCity Server and Agent volumes ==="


declare -A volumes=(
    ["TeamCity Server Data"]="infra/docker_compose/teamcity_server/datadir"
    ["TeamCity Server Logs"]="infra/docker_compose/teamcity_server/logs"
    ["TeamCity Agent Conf"]="infra/docker_compose/teamcity_agent/conf"
    ["TeamCity Agent Logs"]="infra/docker_compose/teamcity_agent/logs"
)

for name in "${!volumes[@]}"; do
    dir="${volumes[$name]}"
    if [ -d "$dir" ]; then
        echo "Checking $name directory: $dir"
        sudo chown -R 1000:1000 "$dir"
        chmod -R u+rwX "$dir"
        echo "✅ Permissions fixed for $name"
    else
        echo "⚠️ Directory $dir does not exist, skipping..."
    fi
done

echo "=== Permissions check finished ==="
echo

docker compose up -d

echo
echo "===== all logs server (last 50 lines) ====="
docker compose logs teamcity-server

echo
echo "===== TeamCity Agent logs (last 50 lines) ====="
docker compose logs --tail 50 teamcity-agent

echo
echo "===== Selenoid logs (last 50 lines) ====="
docker compose logs --tail 50 selenoid

echo
echo "===== Selenoid UI logs (last 50 lines) ====="
docker compose logs --tail 50 selenoid-ui

echo "Waiting for TeamCity..."
for i in {1..60}; do
  if curl -s http://localhost:8111 > /dev/null; then
    echo "TeamCity is ready!"
    break
  fi
  echo "TeamCity not ready yet, waiting 5 seconds..."
  sleep 5
done
