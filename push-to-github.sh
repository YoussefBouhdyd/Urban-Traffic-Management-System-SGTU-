#!/bin/bash
# =============================================================================
# GitHub Push Helper Script
# =============================================================================

echo "🚀 GitHub Repository Setup Helper"
echo "=================================="
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if git is initialized
if [ ! -d ".git" ]; then
    echo -e "${RED}❌ Git repository not initialized${NC}"
    echo "Run: git init"
    exit 1
fi

# Check if remote exists
REMOTE_EXISTS=$(git remote -v | grep origin)

if [ -z "$REMOTE_EXISTS" ]; then
    echo -e "${YELLOW}⚠️  No remote repository configured${NC}"
    echo ""
    echo "Please follow these steps:"
    echo ""
    echo -e "${BLUE}1. Create a new repository on GitHub${NC}"
    echo "   - Go to: https://github.com/new"
    echo "   - Name: integrated-traffic-system (or your preferred name)"
    echo "   - Description: Integrated Smart City Traffic Management System"
    echo "   - Choose: Public or Private"
    echo "   - DO NOT initialize with README, .gitignore, or license"
    echo ""
    echo -e "${BLUE}2. Copy your repository URL${NC}"
    echo "   - Example: https://github.com/yourusername/integrated-traffic-system.git"
    echo ""
    read -p "Enter your GitHub repository URL: " REPO_URL
    
    if [ -z "$REPO_URL" ]; then
        echo -e "${RED}❌ No URL provided${NC}"
        exit 1
    fi
    
    echo ""
    echo -e "${YELLOW}Adding remote repository...${NC}"
    git remote add origin "$REPO_URL"
    echo -e "${GREEN}✓ Remote added${NC}"
else
    echo -e "${GREEN}✓ Remote repository already configured${NC}"
    git remote -v
fi

echo ""
echo -e "${YELLOW}Checking current branch...${NC}"
CURRENT_BRANCH=$(git branch --show-current)
echo "Current branch: $CURRENT_BRANCH"

echo ""
echo -e "${YELLOW}Pushing to GitHub...${NC}"
echo "This may take a few minutes for the first push..."
echo ""

# Push to GitHub
if git push -u origin "$CURRENT_BRANCH"; then
    echo ""
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}✓ Successfully pushed to GitHub!${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""
    echo "Your repository is now available on GitHub!"
    echo ""
    echo "Next steps:"
    echo "  1. Visit your repository on GitHub"
    echo "  2. Add a description and topics"
    echo "  3. Consider adding a LICENSE file"
    echo "  4. Review the README.md"
    echo ""
else
    echo ""
    echo -e "${RED}========================================${NC}"
    echo -e "${RED}❌ Push failed${NC}"
    echo -e "${RED}========================================${NC}"
    echo ""
    echo "Common issues:"
    echo "  1. Authentication required:"
    echo "     - Use GitHub Personal Access Token"
    echo "     - Or set up SSH keys"
    echo ""
    echo "  2. Repository doesn't exist:"
    echo "     - Create it on GitHub first"
    echo ""
    echo "  3. Branch name mismatch:"
    echo "     - Try: git push -u origin main"
    echo ""
    exit 1
fi
